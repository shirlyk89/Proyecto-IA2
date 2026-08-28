# 01 — Propuesta

## Problema

LogiTrack S.A. tiene un backend de Spring Boot para bodegas, productos y
movimientos de inventario, pero la información se revisa manualmente. No
existe una vista diaria que ayude a detectar productos en riesgo de faltante
ni a preparar una compra antes de que el problema se vuelva crítico.

## Objetivo general

Extender el backend existente de LogiTrack con una torre de control que:

1. Calcule el inventario real a partir de los movimientos registrados (no de
   un campo de stock desactualizado).
2. Detecte productos por debajo de su punto de reorden.
3. Permita que un flujo automatizado (n8n, mediante MCP) proponga una orden
   de compra en estado BORRADOR.
4. Permita que un administrador apruebe y reciba esa orden.
5. Refleje el resultado (inventario actualizado, orden recibida) en un
   dashboard real.

## Alcance

- Nuevas entidades: `Proveedor`, `OrdenCompra`, `ResumenPanel`, y la relación
  `proveedorPrincipal` en `Producto`.
- Cálculo de stock, consumo diario, punto de reorden y días de cobertura
  desde los movimientos (`Movimiento`), no desde `Producto.stock`.
- 13 endpoints REST nuevos (`/kpis`, `/productos/{id}/stock`,
  `/productos/riesgo`, `/bodegas/criticas`, `/proveedores`, `/ordenes` y sus
  variantes, `/panel/resumen`).
- Generación de PDF de la orden con marca de agua diagonal cuando está en
  BORRADOR.
- Rol `AGENTE` con permisos restringidos (solo lectura, crear borrador,
  publicar resumen — nunca aprobar/cancelar/recibir).
- Servidor MCP con exactamente 6 herramientas, sin herramienta para aprobar
  órdenes.
- Skill (`SKILL.md`) y flujo único de n8n (`Resumen diario de inventario`).
- Dashboard en HTML/CSS/JS sin framework, conectado a la API real.

## Fuera de alcance

- No se reemplaza ni se crea un backend independiente del proyecto anterior
  de LogiTrack.
- No se implementa autenticación externa (OAuth, SSO) — se reutiliza el JWT
  existente.
- No se exige diseño visual avanzado ni interfaz móvil en el dashboard.
- El MCP no accede directamente a MySQL/Postgres ni implementa reglas de
  negocio propias: siempre pasa por la API REST.
- No se implementa versionado histórico de los resúmenes del panel: cada
  fecha tiene un único resumen vigente (se reemplaza, no se acumulan
  versiones).
