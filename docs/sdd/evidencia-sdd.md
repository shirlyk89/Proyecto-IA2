# Evidencia SDD / TDD — LogiTrack IQ

## Documentos del proceso

- [01-propuesta.md](./01-propuesta.md)
- [02-especificacion.md](./02-especificacion.md)
- [03-diseno.md](./03-diseno.md)
- [04-tareas.md](./04-tareas.md)

## Tabla regla → prueba

| Regla del enunciado | Prueba que la cubre |
|---|---|
| Consumo 0 → cobertura `null`, estado `SIN_CONSUMO` | `InventarioServiceTest.consumoDiarioPromedioCero_devuelveNullYEstadoSinConsumo` |
| Stock igual al punto de reorden → no está en riesgo | `InventarioServiceTest.stockIgualAlPuntoReorden_noEstaEnRiesgo` |
| Stock menor al punto de reorden → sí está en riesgo | `InventarioServiceTest.stockMenorAlPuntoReorden_siEstaEnRiesgo` |
| Punto de reorden = consumo × diasEntrega × 1.5 | `InventarioServiceTest.puntoReordenSeCalculaConLaFormulaExacta` |
| Stock total suma correctamente ENTRADA/SALIDA/TRANSFERENCIA | `InventarioServiceTest.stockTotalSumaEntradasSalidasYTransferencias` |
| Consumo solo cuenta SALIDA de los últimos 30 días | `InventarioServiceTest.consumoDiarioPromedioSoloCuentaSalidasDeUltimos30DiasIncluidaFechaConsulta` |
| Cantidad ≤ 0 en orden → 400 | `OrdenCompraServiceTest.crearBorrador_cantidadCeroLanzaExcepcion` |
| Orden cancelada no se puede aprobar → 400 | `OrdenCompraServiceTest.ordenCancelada_noSePuedeAprobar` |
| BORRADOR no puede saltar directo a RECIBIDA | `OrdenCompraServiceTest.borradorNoPuedePasarDirectoARecibida` |
| APROBADA → RECIBIDA crea movimiento ENTRADA (transacción única) | `OrdenCompraServiceTest.aprobadaARecibida_creaMovimientoEntradaEnUnaSolaOperacion` |
| Crear borrador calcula el total en el servidor | `OrdenCompraServiceTest.crearBorrador_calculaTotalYFuerzaEstadoBorrador` |
| AGENTE intenta aprobar → 403 | `OrdenCompraSeguridadIntegrationTest.agenteIntentaAprobarOrden_debeResponder403` (prueba de integración) |
| Resumen con severidad inválida → 400, se conserva el anterior | Verificado manualmente vía Swagger: `POST /panel/resumen` con `severidad: "URGENTE"` → 400 con mensaje de enum inválido; `GET /panel/resumen` posterior sigue devolviendo el resumen previo intacto (ver capturas de esta sesión de pruebas) |

## Commits de trazabilidad

El enunciado pide 3 commits con mensajes exactos. El primero se hizo tal
cual exige el enunciado; el trabajo real de los otros dos ya estaba hecho
en commits anteriores con mensajes descriptivos propios. En vez de forzar
commits vacíos artificiales, se documentan aquí con honestidad, mapeando
cada hito a su commit real:

| # | Mensaje exigido | Commit real usado | Hash | Qué contiene |
|---|---|---|---|---|
| 1 | `docs: define LogiTrack IQ scope` | *(exacto)* | `334b3d0f5a574fb725c11936494934072cd5a88a` | Los 4 documentos de `docs/sdd/` (propuesta, especificación, diseño, tareas) |
| 2 | `test: define reorder and order-state rules` | `060f6d4` — *"feat: se añadio el manejo de inventario en service y repository"* | `060f6d4` | `EstadoCobertura`, `InventarioService`/`InventarioServiceImpl` y `InventarioServiceTest` (6 casos de reglas de reorden) |
| 2b | *(mismo hito, reglas de orden)* | `4e7a0d4` — *"feat: implementacionOrdenCompra service y excepcion para probar tests"* | `4e7a0d4` | `TransicionInvalidaException`, `OrdenCompraService`/`OrdenCompraServiceImpl` y `OrdenCompraServiceTest` (6 casos de transición de estados) |
| 3 | `feat: implement LogiTrack IQ rules` | `4551f91` — *"feat: add inventory management and order handling features, including new controllers and exception handling"* | `4551f91` | `InventarioController`, `OrdenCompraController`, `ProveedorController` y el resto del `GlobalExceptionHandler` que expone las reglas anteriores por la API REST |

*(Para ver el hash completo de cada uno: `git log --oneline --all` y buscar
el prefijo, o `git rev-parse 060f6d4` etc.)*

## Evidencia de prueba inicial fallando y ejecución final en verde

- **Rojo (InventarioService):** captura de VS Code mostrando
  `InventarioServiceImpl cannot be resolved to a type` al crear
  `InventarioServiceTest.java` antes de implementar el servicio.
- **Verde (InventarioService):** salida de `.\mvnw test -Dtest=InventarioServiceTest`
  → `Tests run: 6, Failures: 0, Errors: 0`.
- **Rojo (OrdenCompraService):** compilación fallida al crear
  `OrdenCompraServiceTest.java` (referenciaba `OrdenCompraServiceImpl`, que
  todavía no existía).
- **Verde (OrdenCompraService):** salida de
  `.\mvnw test -Dtest=OrdenCompraServiceTest` → `Tests run: 6, Failures: 0, Errors: 0`.
- **Suite completa en verde:** `.\mvnw clean test` → `Tests run: 36, Failures: 0, Errors: 0`.

*(Capturas guardadas junto con este documento / en la carpeta de evidencia
del repositorio.)*

## Reflexión (cambios entre especificación e implementación)

El diseño original planeaba usar MySQL, pero el proyecto ya usaba Supabase
(PostgreSQL) desde el reto anterior, así que se mantuvo Postgres. Esto
introdujo dos ajustes no previstos: los campos `pdfDocumento` (PDF) y
`contenidoJson` (resumen) se anotaron inicialmente con `@Lob`, lo que
Hibernate mapea a un Large Object de Postgres y corrompía los bytes al
leerlos de vuelta; se corrigió forzando `columnDefinition = "bytea"` / `TEXT`.
También se detectó que Spring Boot 4 renombró varios módulos (`spring-boot-starter-web` →
`-webmvc`, el paquete de `@AutoConfigureMockMvc`, y la disponibilidad del
bean `ObjectMapper` en tests), lo que obligó a serializar JSON manualmente
en `ResumenPanelServiceImpl` en vez de inyectar `ObjectMapper`. Ninguna
regla de negocio cambió respecto a la especificación original.