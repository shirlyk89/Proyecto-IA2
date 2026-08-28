# LogiTrack MCP Server

Servidor MCP que expone 6 herramientas de solo-lectura-y-una-escritura-controlada
sobre la API REST de LogiTrack IQ. Usa siempre un usuario con rol **AGENTE**
(nunca ADMIN), y **no existe una herramienta para aprobar, cancelar o recibir
órdenes** — esa restricción es intencional y obligatoria.

## Requisitos

- Node.js 18 o superior (usa el `fetch` nativo).
- El backend de LogiTrack corriendo y accesible (por defecto en `http://localhost:8081/api`).
- Un usuario con rol `AGENTE` ya creado en la base de datos (regístralo una vez
  vía `POST /api/auth/register` con `{"username": "...", "password": "...", "rol": "AGENTE"}`).

## Instalación

```bash
cd mcp-server
npm install
cp .env.example .env
# Edita .env con la URL del backend y las credenciales del usuario AGENTE
```

## Ejecución

```bash
npm start
```

El servidor se comunica por **stdio**, que es el transporte estándar para
clientes MCP locales (Claude Desktop, y también configurable como comando en
el nodo AI Agent de n8n).

## Herramientas

| Herramienta | Endpoint que usa | Parámetros |
|---|---|---|
| `consultar_stock_producto` | `GET /productos/{id}/stock` | `productoId` |
| `consultar_bodegas_criticas` | `GET /bodegas/criticas` | — |
| `consultar_productos_en_riesgo` | `GET /productos/riesgo` | — |
| `consultar_kpis` | `GET /kpis` | — |
| `crear_orden_borrador` | `POST /ordenes` | `productoId, proveedorId, bodegaDestinoId, cantidad, precioUnitario` |
| `publicar_resumen` | `POST /panel/resumen` | `resumen` (objeto que cumple el contrato) |

## Evidencia de entrada/salida de cada herramienta

*(Completar aquí con una captura o el JSON de entrada/salida real de cada
herramienta al probarla — es un entregable obligatorio del proyecto. Puedes
probar el servidor directamente con el inspector oficial de MCP:)*

```bash
npx @modelcontextprotocol/inspector node src/index.js
```

Esto abre una interfaz web donde puedes ejecutar cada una de las 6
herramientas manualmente, ver el request/response, y guardar capturas.