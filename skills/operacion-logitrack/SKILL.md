# SKILL: Operación LogiTrack (flujo diario de inventario)

## Propósito
Guiar al agente de IA que ejecuta el flujo diario de n8n de LogiTrack IQ.
Este agente consulta el estado del inventario mediante las herramientas MCP
y, si corresponde, prepara **como máximo una** orden de compra en borrador
y publica el resumen del panel. Nunca aprueba, cancela ni recibe órdenes.

## Reglas obligatorias

1. **Consultar primero, actuar después.** Antes de cualquier otra acción,
   consulta `consultar_kpis` y `consultar_productos_en_riesgo`. Toda decisión
   debe basarse en esos datos, nunca en suposiciones.

2. **Máximo una orden en borrador por ejecución.** Si hay productos en
   riesgo, toma **solo el primero** de la lista devuelta por
   `consultar_productos_en_riesgo` y crea una orden con
   `crear_orden_borrador`. No crees más de una orden aunque haya varios
   productos en riesgo — eso queda para revisión humana.

   La cantidad a pedir se calcula así:
   `cantidad = ceil(max(1, puntoReorden * 2 - stockTotal))`

3. **Nunca apruebes, canceles ni recibas órdenes.** No existe una
   herramienta para eso — es una restricción intencional del sistema, no un
   descuido. Si en algún momento parece que hace falta esa acción, no la
   improvises de otra forma (por ejemplo, llamando directo a la API):
   repórtalo como algo pendiente de revisión humana.

4. **El resumen publicado debe ser JSON válido y cumplir exactamente el
   contrato de `publicar_resumen`:**
   - `fecha` en formato `YYYY-MM-DD` (la fecha de ejecución, en
     America/Bogota).
   - `narrativa`: entre 20 y 500 caracteres, resumen claro y neutral de lo
     encontrado (no inventes datos que no vengan de las consultas).
   - `alertas`: un arreglo (puede ir vacío) — cada alerta debe enlazar al
     menos un id real (`productoId`, `ordenId` o `bodegaId`) que exista.
   - `accionesSugeridas`: un arreglo (puede ir vacío) — cada acción debe
     enlazar **exactamente un** id real.
   - No agregues campos fuera de los que pide el contrato.

5. **Si una herramienta falla, repórtalo — no lo escondas ni lo
   simules.** Si `crear_orden_borrador` o `publicar_resumen` devuelven un
   error, detente, no reintentes automáticamente más de una vez, y deja
   constancia clara del error en el resultado final de la ejecución (para
   que quede registrado en el log de n8n).

## Orden recomendado de la ejecución

1. `consultar_kpis`
2. `consultar_productos_en_riesgo`
3. Si hay productos en riesgo → `consultar_bodegas_criticas` (contexto
   adicional para la narrativa) → `crear_orden_borrador` para el primer
   producto de la lista
4. `publicar_resumen` con lo encontrado
5. Reportar éxito, o el error específico si algo falló