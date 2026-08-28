# 04 — Tareas

## Modelo y reglas de negocio
- [x] Entidades `Proveedor`, `OrdenCompra`, `ResumenPanel`, enums `EstadoOrden`/`EstadoCobertura`
- [x] Relación `proveedorPrincipal` en `Producto`
- [x] `InventarioService`: stock por bodega, stock total, consumo diario, punto de reorden, días de cobertura, estado de cobertura
- [x] Tests de `InventarioService` (6 casos, incluye consumo 0 y stock == punto de reorden)
- [x] `OrdenCompraService`: creación en BORRADOR, transición de estados, movimiento ENTRADA transaccional
- [x] Tests de `OrdenCompraService` (6 casos, incluye cantidad inválida y transición prohibida)
- [x] Rol `AGENTE` + matriz de permisos en `SecurityConfig`
- [x] Prueba de integración: AGENTE intenta aprobar → 403

## API REST
- [x] `GET /kpis`
- [x] `GET /productos/{id}/stock`
- [x] `GET /productos/riesgo`
- [x] `GET /bodegas/criticas`
- [x] `GET /proveedores`
- [x] `GET /ordenes`, `POST /ordenes`, `GET /ordenes/{id}`, `PATCH /ordenes/{id}/estado`
- [x] `POST /panel/resumen`, `GET /panel/resumen` (con validación completa del contrato)
- [x] `POST /ordenes/{id}/pdf`, `GET /ordenes/{id}/pdf`
- [x] Manejo global de errores (400/404 para las excepciones nuevas)

## PDF
- [x] Generación con PDFBox
- [x] Marca de agua diagonal condicional a BORRADOR
- [x] PDF se invalida al cambiar el estado

## MCP + skill + n8n
- [x] Servidor MCP con las 6 herramientas obligatorias
- [x] Sin herramienta para aprobar órdenes
- [x] Transporte `stdio` (pruebas locales) y HTTP/SSE (para n8n)
- [x] `SKILL.md` con las reglas obligatorias
- [x] Flujo n8n "Resumen diario de inventario" (Schedule 6am + AI Agent + MCP Client Tool)
- [x] Ejecución exitosa capturada
- [x] Ejecución con error controlado capturada

## Frontend
- [x] Login JWT (token solo en sessionStorage)
- [x] KPIs, ocupación por bodega, movimientos de ayer
- [x] Narrativa/alertas/acciones del resumen
- [x] Tabla de productos en riesgo
- [x] Tabla de órdenes en BORRADOR
- [x] Generar/ver PDF con marca de agua
- [x] Botón "Aprobar" visible solo para ADMIN, refresca tabla tras aprobar

## Documentación y evidencia
- [x] `docs/sdd/01-propuesta.md`
- [x] `docs/sdd/02-especificacion.md`
- [x] `docs/sdd/03-diseno.md`
- [x] `docs/sdd/04-tareas.md` (este documento)
- [ ] `docs/sdd/evidencia-sdd.md` (trazabilidad, hashes, reflexión)
- [ ] README final (instalación, usuarios de prueba, rutas)
- [ ] `data.sql` / `schema.sql` limpios y reproducibles
- [ ] Diagrama exportado como imagen
- [ ] Video de 4–6 minutos