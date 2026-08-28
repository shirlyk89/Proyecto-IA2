// ============================================================
// LogiTrack IQ — Dashboard (HTML/CSS/JS sin framework)
// ============================================================

const API_BASE = 'http://localhost:8081/api';

// ---------- Estado de sesión (solo sessionStorage, como exige el enunciado) ----------
function getToken() { return sessionStorage.getItem('lt_token'); }
function getRol() { return sessionStorage.getItem('lt_rol') || ''; }
function getUsername() { return sessionStorage.getItem('lt_username') || ''; }
function isAdmin() { return getRol().toUpperCase().includes('ADMIN'); }

function setSession(token, rol, username) {
  sessionStorage.setItem('lt_token', token);
  sessionStorage.setItem('lt_rol', rol);
  sessionStorage.setItem('lt_username', username);
}
function clearSession() { sessionStorage.clear(); }

// ---------- Cliente API ----------
async function api(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}),
      ...(options.headers || {}),
    },
  });

  if (res.status === 401) {
    clearSession();
    showLogin('Tu sesión expiró. Vuelve a iniciar sesión.');
    throw new Error('401 Unauthorized');
  }

  if (!res.ok) {
    let mensaje = `Error ${res.status}`;
    try {
      const body = await res.json();
      mensaje = body.message || mensaje;
    } catch (_) { /* respuesta sin cuerpo JSON */ }
    throw new Error(mensaje);
  }

  const contentType = res.headers.get('content-type') || '';
  if (contentType.includes('application/json')) return res.json();
  return res.text();
}

async function apiBlob(path, options = {}) {
  const res = await fetch(`${API_BASE}${path}`, {
    ...options,
    headers: {
      ...(getToken() ? { Authorization: `Bearer ${getToken()}` } : {}),
      ...(options.headers || {}),
    },
  });
  if (!res.ok) throw new Error(`Error ${res.status} al obtener el PDF`);
  return res.blob();
}

// ---------- Vistas ----------
const loginView = document.getElementById('login-view');
const dashboardView = document.getElementById('dashboard-view');

function showLogin(errorMsg) {
  dashboardView.hidden = true;
  loginView.hidden = false;
  const errEl = document.getElementById('login-error');
  if (errorMsg) {
    errEl.textContent = errorMsg;
    errEl.hidden = false;
  } else {
    errEl.hidden = true;
  }
}

function showDashboard() {
  loginView.hidden = true;
  dashboardView.hidden = false;
  document.getElementById('user-badge').textContent = `${getUsername()} · ${getRol().replace('ROLE_', '')}`;
  loadAll();
}

// ---------- Login ----------
document.getElementById('login-form').addEventListener('submit', async (e) => {
  e.preventDefault();
  const username = document.getElementById('username').value.trim();
  const password = document.getElementById('password').value;
  const btn = document.getElementById('login-btn');
  btn.disabled = true;
  btn.textContent = 'Entrando…';

  try {
    const data = await api('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ username, password }),
    });
    setSession(data.token, data.rol || '', username);
    showDashboard();
  } catch (err) {
    document.getElementById('login-error').textContent = 'Usuario o contraseña incorrectos.';
    document.getElementById('login-error').hidden = false;
  } finally {
    btn.disabled = false;
    btn.textContent = 'Entrar';
  }
});

document.getElementById('logout-btn').addEventListener('click', () => {
  clearSession();
  showLogin();
});

// ---------- Carga de datos ----------
let bodegasCache = {}; // id -> nombre, para mostrar nombres legibles en la tabla de riesgo

async function loadAll() {
  await Promise.all([
    loadBodegasCache(),
    loadKpis(),
    loadResumen(),
  ]);
  await Promise.all([
    loadRiesgo(),
    loadOrdenesBorrador(),
  ]);
}

async function loadBodegasCache() {
  try {
    const bodegas = await api('/bodegas');
    bodegasCache = Object.fromEntries(bodegas.map((b) => [b.id, b.nombre]));
  } catch (_) { /* si falla, se muestran solo los IDs */ }
}

function fmtMoney(n) {
  if (n === null || n === undefined) return '—';
  return new Intl.NumberFormat('es-CO', { maximumFractionDigits: 2 }).format(n);
}

async function loadKpis() {
  try {
    const kpis = await api('/kpis');

    document.getElementById('kpi-riesgo').textContent = kpis.productosEnRiesgo ?? '—';
    document.getElementById('kpi-quiebre').textContent = kpis.productosEnQuiebre ?? '—';
    document.getElementById('kpi-ordenes-cantidad').textContent = kpis.ordenesPorAprobar?.cantidad ?? '—';
    document.getElementById('kpi-ordenes-monto').textContent =
      kpis.ordenesPorAprobar?.montoTotal != null ? `$${fmtMoney(kpis.ordenesPorAprobar.montoTotal)}` : '';

    document.getElementById('mov-entrada').textContent = kpis.movimientosAyer?.entrada ?? '—';
    document.getElementById('mov-salida').textContent = kpis.movimientosAyer?.salida ?? '—';
    document.getElementById('mov-transferencia').textContent = kpis.movimientosAyer?.transferencia ?? '—';

    document.getElementById('calculated-at').textContent = kpis.calculadoEn
      ? `Actualizado ${new Date(kpis.calculadoEn).toLocaleString('es-CO')}`
      : '';

    const list = document.getElementById('ocupacion-list');
    list.innerHTML = '';
    (kpis.ocupacionPorBodega || []).forEach((b) => {
      const pct = Number(b.porcentaje) || 0;
      const pctClamped = Math.max(0, Math.min(100, pct));
      const row = document.createElement('div');
      row.className = 'ocupacion-row';
      row.innerHTML = `
        <span class="ocupacion-nombre">${b.nombre}</span>
        <span class="ocupacion-bar-track">
          <span class="ocupacion-bar-fill ${pct >= 90 ? 'critica' : ''}" style="width:${pctClamped}%"></span>
        </span>
        <span class="ocupacion-pct">${pct.toFixed(1)}%</span>
      `;
      list.appendChild(row);
    });
  } catch (err) {
    console.error('Error cargando KPIs', err);
  }
}

async function loadResumen() {
  const narrativaEl = document.getElementById('briefing-narrativa');
  const fechaEl = document.getElementById('briefing-fecha');
  const alertasEl = document.getElementById('briefing-alertas');
  const accionesEl = document.getElementById('briefing-acciones');

  try {
    const resumen = await api('/panel/resumen');
    const contenido = JSON.parse(resumen.contenidoJson);

    fechaEl.textContent = resumen.fecha;
    narrativaEl.textContent = contenido.narrativa;

    alertasEl.innerHTML = '';
    if (!contenido.alertas || contenido.alertas.length === 0) {
      alertasEl.innerHTML = '<li class="briefing-empty">Sin alertas.</li>';
    } else {
      contenido.alertas.forEach((a) => {
        const li = document.createElement('li');
        li.className = `sev-${a.severidad}`;
        li.textContent = `[${a.severidad}] ${a.titulo} — ${a.detalle}`;
        alertasEl.appendChild(li);
      });
    }

    accionesEl.innerHTML = '';
    if (!contenido.accionesSugeridas || contenido.accionesSugeridas.length === 0) {
      accionesEl.innerHTML = '<li class="briefing-empty">Sin acciones sugeridas.</li>';
    } else {
      contenido.accionesSugeridas.forEach((a) => {
        const li = document.createElement('li');
        li.textContent = `${a.tipo.replace('REVISAR_', '')}: ${a.descripcion}`;
        accionesEl.appendChild(li);
      });
    }
  } catch (err) {
    // 404 = todavía no hay resumen publicado; se deja el estado por defecto del HTML
    fechaEl.textContent = '';
    alertasEl.innerHTML = '<li class="briefing-empty">—</li>';
    accionesEl.innerHTML = '<li class="briefing-empty">—</li>';
  }
}

function pillEstadoCobertura(estado) {
  const map = {
    EN_RIESGO: 'pill-riesgo',
    SIN_CONSUMO: 'pill-sin-consumo',
    NORMAL: 'pill-normal',
  };
  return `<span class="pill ${map[estado] || 'pill-normal'}">${estado}</span>`;
}

async function loadRiesgo() {
  const body = document.getElementById('tabla-riesgo-body');
  try {
    const productos = await api('/productos/riesgo');
    if (!productos.length) {
      body.innerHTML = '<tr><td colspan="7" class="empty-row">No hay productos en riesgo en este momento.</td></tr>';
      return;
    }
    body.innerHTML = productos.map((p) => `
      <tr>
        <td>${p.nombreProducto}</td>
        <td><b>${p.stockTotal}</b></td>
        <td>${p.consumoDiarioPromedio != null ? Number(p.consumoDiarioPromedio).toFixed(2) : '—'}</td>
        <td>${p.puntoReorden != null ? Number(p.puntoReorden).toFixed(2) : '—'}</td>
        <td>${p.diasCobertura != null ? Number(p.diasCobertura).toFixed(1) : '—'}</td>
        <td>${pillEstadoCobertura(p.estadoCobertura)}</td>
        <td>${bodegasCache[p.bodegaDestinoId] || `Bodega #${p.bodegaDestinoId}`}</td>
      </tr>
    `).join('');
  } catch (err) {
    body.innerHTML = `<tr><td colspan="7" class="empty-row">Error al cargar: ${err.message}</td></tr>`;
  }
}

async function loadOrdenesBorrador() {
  const body = document.getElementById('tabla-ordenes-body');
  try {
    const ordenes = await api('/ordenes?estado=BORRADOR');
    if (!ordenes.length) {
      body.innerHTML = '<tr><td colspan="8" class="empty-row">No hay órdenes en borrador.</td></tr>';
      return;
    }
    body.innerHTML = ordenes.map((o) => `
      <tr data-id="${o.id}">
        <td>#${o.id}</td>
        <td>${o.producto?.nombre ?? '—'}</td>
        <td>${o.proveedor?.nombre ?? '—'}</td>
        <td>${o.cantidad}</td>
        <td>$${fmtMoney(o.total)}</td>
        <td>${o.bodegaDestino?.nombre ?? '—'}</td>
        <td><button class="btn-secondary btn-ver-pdf" data-id="${o.id}">Ver PDF</button></td>
        <td>${isAdmin() ? `<button class="btn-primary btn-aprobar" data-id="${o.id}">Aprobar</button>` : ''}</td>
      </tr>
    `).join('');

    body.querySelectorAll('.btn-ver-pdf').forEach((btn) => {
      btn.addEventListener('click', () => verPdf(btn.dataset.id));
    });
    body.querySelectorAll('.btn-aprobar').forEach((btn) => {
      btn.addEventListener('click', () => aprobarOrden(btn.dataset.id, btn));
    });
  } catch (err) {
    body.innerHTML = `<tr><td colspan="8" class="empty-row">Error al cargar: ${err.message}</td></tr>`;
  }
}

// ---------- Acciones ----------
async function verPdf(ordenId) {
  const overlay = document.getElementById('pdf-overlay');
  const frame = document.getElementById('pdf-frame');
  const title = document.getElementById('pdf-modal-title');
  title.textContent = `Generando PDF de la orden #${ordenId}…`;
  frame.src = 'about:blank';
  overlay.hidden = false;

  try {
    // Genera (o regenera) el PDF antes de mostrarlo, para reflejar el estado actual.
    await api(`/ordenes/${ordenId}/pdf`, { method: 'POST' });
    const blob = await apiBlob(`/ordenes/${ordenId}/pdf`);
    const url = URL.createObjectURL(blob);
    frame.src = url;
    title.textContent = `PDF de la orden #${ordenId}`;
  } catch (err) {
    title.textContent = `No se pudo generar el PDF: ${err.message}`;
  }
}

document.getElementById('pdf-close-btn').addEventListener('click', () => {
  document.getElementById('pdf-overlay').hidden = true;
  document.getElementById('pdf-frame').src = 'about:blank';
});

async function aprobarOrden(ordenId, btn) {
  btn.disabled = true;
  btn.textContent = 'Aprobando…';
  try {
    await api(`/ordenes/${ordenId}/estado`, {
      method: 'PATCH',
      body: JSON.stringify({ estado: 'APROBADA' }),
    });
    // La orden ya no está en BORRADOR: refresca la tabla y los KPIs.
    await Promise.all([loadOrdenesBorrador(), loadKpis()]);
  } catch (err) {
    alert(`No se pudo aprobar la orden: ${err.message}`);
    btn.disabled = false;
    btn.textContent = 'Aprobar';
  }
}

// ---------- Arranque ----------
if (getToken()) {
  showDashboard();
} else {
  showLogin();
}