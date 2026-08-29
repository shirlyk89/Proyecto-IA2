# LogiTrack IQ — Torre de control de inventario

Extensión del backend de LogiTrack (Spring Boot) que detecta productos en
riesgo de faltante, propone una orden de compra en BORRADOR mediante un
flujo automatizado (n8n + MCP), y permite a un administrador aprobarla y
recibirla — reflejando todo en un dashboard conectado a datos reales.

## Arquitectura

```
n8n → MCP Server (6 tools) → API Spring Boot (JWT, rol AGENTE) → PostgreSQL
                                       ↑
                                  Dashboard (HTML/CSS/JS) ← Administrador
```

Ver diagrama completo en [`diagrama-arquitectura.svg`](./diagrama-arquitectura.svg)
y el detalle de diseño en [`docs/sdd/03-diseno.md`](./docs/sdd/03-diseno.md).

## Requisitos

- Docker Desktop
- Node.js 18+ (solo si quieres correr el MCP server o el frontend fuera de Docker)
- Una cuenta de Supabase/PostgreSQL ya configurada (las credenciales van en `.env`, no se suben al repo)

## Instalación y ejecución

1. Clona el repositorio y entra a la carpeta del proyecto.
2. Crea un archivo `.env` en la raíz con:
   ```
   DB_PASSWORD=<contraseña real de la base de datos>
   AGENTE_PASSWORD=<contraseña del usuario AGENTE de prueba>
   ```
3. Levanta todo con Docker:
   ```
   docker compose up --build
   ```
   Esto levanta 3 servicios:
   - **backend** — API Spring Boot en `http://localhost:8081`
   - **mcp-server** — servidor MCP (HTTP/SSE) en `http://localhost:3939`
   - **n8n** — editor de flujos en `http://localhost:5678`
4. Abre el dashboard: `frontend/index.html` con doble clic en el navegador
   (no necesita servidor propio, consume la API directamente).
5. Swagger/OpenAPI de la API: `http://localhost:8081/swagger-ui/index.html`

## Usuarios de prueba

| Usuario | Rol | Notas |
|---|---|---|
| `admin1` | ADMIN | Puede aprobar/recibir/cancelar órdenes y registrar movimientos manuales |
| `agente1` | AGENTE | Solo lectura + crear borrador + publicar resumen (usado por el MCP server) |

*(Ajusta o crea estos usuarios vía `POST /api/auth/register` si no existen
todavía en tu base de datos — ver `docs/sdd/02-especificacion.md` para la
matriz completa de permisos.)*

## Rutas principales de la API

| Método y ruta | Qué hace |
|---|---|
| `POST /api/auth/register`, `POST /api/auth/login` | Registro y login (JWT) |
| `GET /api/kpis` | Los 4 indicadores del dashboard |
| `GET /api/productos/{id}/stock` | Stock total y por bodega de un producto |
| `GET /api/productos/riesgo` | Productos por debajo del punto de reorden |
| `GET /api/bodegas/criticas` | Bodegas con ocupación ≥ 90% |
| `GET /api/proveedores` | Proveedores precargados |
| `GET /api/ordenes`, `POST /api/ordenes` | Listar / crear orden en BORRADOR |
| `PATCH /api/ordenes/{id}/estado` | Cambiar estado de una orden (solo ADMIN para aprobar/recibir/cancelar) |
| `POST /api/ordenes/{id}/pdf`, `GET /api/ordenes/{id}/pdf` | Generar y descargar el PDF de la orden |
| `POST /api/panel/resumen`, `GET /api/panel/resumen` | Publicar y consultar el resumen diario |

## Estructura del repositorio

```
src/                    Backend Spring Boot (entidades, servicios, controllers, tests)
frontend/               Dashboard HTML/CSS/JS
mcp-server/             Servidor MCP (Node.js) con las 6 herramientas
n8n/                    Export del flujo "Resumen diario de inventario"
skills/operacion-logitrack/SKILL.md   Reglas operativas del agente
docs/sdd/                Documentos de diseño y evidencia SDD/TDD
docker-compose.yml       Orquesta backend + mcp-server + n8n
schema.sql, data.sql     Estructura y datos de prueba reproducibles
```

## Correr los tests

```
.\mvnw clean test
```

## MCP server — probar las herramientas manualmente

```
cd mcp-server
npm install
cp .env.example .env   # y edítalo con tus credenciales AGENTE
npx @modelcontextprotocol/inspector node src/index.js
```

## Flujo de n8n

Importa `n8n/resumen-diario-inventario.json` en tu instancia de n8n
(`http://localhost:5678` → Import from File). Necesitarás conectar tu
propia credencial del modelo de IA (Gemini/OpenAI/Anthropic) en el nodo
"Chat Model" después de importar.