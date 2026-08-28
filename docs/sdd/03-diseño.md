# 03 — Diseño

## Entidades nuevas

| Entidad | Campos clave | Notas |
|---|---|---|
| `Proveedor` | `id, nombre, contacto, diasEntrega` | `diasEntrega` validado entre 1 y 90 |
| `OrdenCompra` | `id, producto, proveedor, bodegaDestino, cantidad, precioUnitario, total, fechaCreacion, estado, creadoPor, pdfDocumento, pdfFechaGeneracion` | `pdfDocumento` como `bytea` (ver decisión abajo) |
| `ResumenPanel` | `id, fecha (unique), contenidoJson, autor` | Un solo resumen vigente por fecha |
| `EstadoOrden` (enum) | `BORRADOR, APROBADA, RECIBIDA, CANCELADA` | |
| `EstadoCobertura` (enum) | `SIN_CONSUMO, EN_RIESGO, NORMAL` | |

`Producto` se extiende con `proveedorPrincipal` (`ManyToOne` opcional a
`Proveedor`).

## Servicios nuevos

- **`InventarioService`**: cálculo de stock (total y por bodega), consumo
  diario promedio, punto de reorden, días de cobertura y estado de
  cobertura — todo desde `MovimientoRepository`, sin tocar `Producto.stock`.
- **`OrdenCompraService`**: creación en BORRADOR (calcula el total en el
  servidor) y transición de estados (con la matriz de transiciones
  validada y la creación transaccional del movimiento ENTRADA).
- **`PanelService`**: agrega `InventarioService` + repositorios para
  construir los KPIs, la lista de productos en riesgo y las bodegas
  críticas.
- **`ResumenPanelService`**: valida el contrato completo del resumen
  (longitud de narrativa, enums, unicidad de identificador por alerta/
  acción, existencia de IDs referenciados) antes de guardar.
- **`PdfService`**: genera el PDF con PDFBox, con marca de agua diagonal
  condicional al estado BORRADOR.

## Decisiones de diseño (y por qué)

1. **El stock nunca se lee de `Producto.stock`.** Se calcula siempre en
   tiempo real recorriendo movimientos, según exige el enunciado. Esto
   significa que `InventarioService` es la única fuente de verdad para
   cualquier número de inventario mostrado en KPIs, riesgo o el dashboard.

2. **`pdfDocumento` y `contenidoJson` se guardan como tipos nativos
   (`bytea` / `TEXT`), no como `@Lob`.** En pruebas se detectó que
   Hibernate + PostgreSQL mapea `@Lob` a un Large Object (`oid`), lo que
   corrompía los bytes del PDF y el JSON al leerlos de vuelta. Se corrigió
   forzando `columnDefinition` explícito.

3. **El servidor MCP nunca llama directo a la base de datos.** Todas sus 6
   herramientas pasan por la API REST autenticada con un usuario `AGENTE`,
   cumpliendo la restricción de que MCP no implemente reglas de negocio
   propias.

4. **El MCP server expone dos transportes** (`stdio` para pruebas locales
   con el Inspector, y HTTP/SSE para que n8n —en otro contenedor Docker—
   pueda conectarse por red). Comparten el mismo módulo de herramientas
   (`tools.js`) para no duplicar lógica.

5. **La skill se copia dentro del *system message* del nodo AI Agent en
   n8n**, ya que el enunciado no exige cargarla dinámicamente — el archivo
   `SKILL.md` es la evidencia mantenible y auditable de esas reglas.

## Diagrama de flujo

```
┌─────────────┐     6:00 AM      ┌──────────────┐
│   n8n        │ ───────────────▶│  AI Agent    │
│ (Schedule)   │                  │ + Skill      │
└─────────────┘                  └──────┬───────┘
                                         │ MCP (HTTP/SSE)
                                         ▼
                                 ┌──────────────┐
                                 │  MCP Server  │
                                 │  (6 tools)   │
                                 └──────┬───────┘
                                         │ REST + JWT (rol AGENTE)
                                         ▼
                                 ┌──────────────┐
                                 │ API Spring   │
                                 │ Boot         │
                                 └──────┬───────┘
                                         │
                                         ▼
                                 ┌──────────────┐
                                 │  PostgreSQL  │
                                 │  (Supabase)  │
                                 └──────┬───────┘
                                         │
                          ┌──────────────┴──────────────┐
                          ▼                              ▼
                 ┌──────────────┐              ┌──────────────────┐
                 │  Dashboard    │◀────────────▶│ Administrador    │
                 │ (HTML/CSS/JS) │   aprueba /   │ (navegador)      │
                 │               │   recibe      │                  │
                 └──────────────┘              └──────────────────┘
```

*(Este mismo diagrama se guarda también como imagen en
`docs/diagrama-arquitectura.png` para el entregable 11.)*