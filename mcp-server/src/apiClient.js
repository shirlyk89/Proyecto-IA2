import 'dotenv/config';

const API_BASE_URL = process.env.API_BASE_URL || 'http://localhost:8081/api';
const AGENTE_USERNAME = process.env.AGENTE_USERNAME;
const AGENTE_PASSWORD = process.env.AGENTE_PASSWORD;

let cachedToken = null;

/** Inicia sesión con el usuario AGENTE y guarda el token en memoria. */
async function login() {
  if (!AGENTE_USERNAME || !AGENTE_PASSWORD) {
    throw new Error(
      'Faltan AGENTE_USERNAME o AGENTE_PASSWORD en el archivo .env del servidor MCP.'
    );
  }

  const res = await fetch(`${API_BASE_URL}/auth/login`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username: AGENTE_USERNAME, password: AGENTE_PASSWORD }),
  });

  if (!res.ok) {
    const texto = await res.text();
    throw new Error(`No se pudo autenticar el usuario AGENTE (${res.status}): ${texto}`);
  }

  const data = await res.json();
  cachedToken = data.token;
  return cachedToken;
}

/**
 * Hace una petición autenticada a la API. Si el token expiró (401), reintenta
 * una vez volviendo a iniciar sesión.
 */
async function apiRequest(path, options = {}) {
  if (!cachedToken) {
    await login();
  }

  const doRequest = async () =>
    fetch(`${API_BASE_URL}${path}`, {
      ...options,
      headers: {
        'Content-Type': 'application/json',
        Authorization: `Bearer ${cachedToken}`,
        ...(options.headers || {}),
      },
    });

  let res = await doRequest();

  if (res.status === 401) {
    // El token pudo haber expirado: reintenta una sola vez con uno nuevo.
    await login();
    res = await doRequest();
  }

  if (!res.ok) {
    const texto = await res.text();
    throw new Error(`Error ${res.status} en ${path}: ${texto}`);
  }

  // Algunas respuestas (204) no traen cuerpo.
  const contentType = res.headers.get('content-type') || '';
  if (contentType.includes('application/json')) {
    return res.json();
  }
  return res.text();
}

export const apiClient = {
  get: (path) => apiRequest(path, { method: 'GET' }),
  post: (path, body) => apiRequest(path, { method: 'POST', body: JSON.stringify(body) }),
};