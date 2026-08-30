-- ============================================================
-- data.sql — LogiTrack IQ
-- Ejecutar UNA VEZ manualmente en DBeaver/Supabase antes de la demo/video.
-- No se ejecuta automáticamente (la base es Supabase, no un contenedor
-- local que se recrea en cada docker compose up).
-- ============================================================

-- 1. LIMPIEZA: quitar ruido de pruebas de esta sesión de desarrollo
-- ------------------------------------------------------------
DELETE FROM logitrack.movimientos WHERE producto_id = 1;
DELETE FROM logitrack.ordenes_compra;
DELETE FROM logitrack.resumenes_panel;
DELETE FROM logitrack.usuarios WHERE username LIKE 'agente_test_%';

-- 2. Asegurar que el producto de demo tenga proveedor principal
--    (sin esto, nunca puede aparecer como "en riesgo", por regla de negocio)
-- ------------------------------------------------------------
UPDATE logitrack.productos SET proveedor_principal_id = 1 WHERE id = 1;
-- (proveedor_principal_id = 1 → "Proveedor Prueba", diasEntrega = 5)

-- 3. Movimientos limpios y reproducibles, relativos a la fecha actual
--    Escenario: 50 unidades de entrada hace 35 días (fuera de la ventana
--    de 30 días de consumo), y 45 unidades de salida distribuidas en los
--    últimos 20 días → stock final = 5, consumo diario ≈ 1.5,
--    punto de reorden ≈ 11.25 → el producto queda EN RIESGO.
-- ------------------------------------------------------------
INSERT INTO logitrack.movimientos (tipo, cantidad, fecha_hora, producto_id, bodega_destino_id, bodega_origen_id, usuario_id)
VALUES
  ('ENTRADA', 50, (CURRENT_DATE - INTERVAL '35 days') + TIME '08:00', 1, 1, NULL, NULL),
  ('SALIDA',  15, (CURRENT_DATE - INTERVAL '20 days') + TIME '09:00', 1, NULL, 1, NULL),
  ('SALIDA',  15, (CURRENT_DATE - INTERVAL '10 days') + TIME '09:00', 1, NULL, 1, NULL),
  ('SALIDA',  15, (CURRENT_DATE - INTERVAL '1 day')   + TIME '09:00', 1, NULL, 1, NULL);

-- ============================================================
-- Resultado esperado al ejecutar esto y luego consultar la API:
--   GET /api/productos/riesgo  → debe mostrar "Laptop HP" (id 1)
--     con stockTotal=5, consumoDiarioPromedio≈1.5, puntoReorden≈11.25,
--     estadoCobertura=EN_RIESGO
--   GET /api/panel/resumen     → 404 hasta que el flujo de n8n publique
--     uno nuevo (así el video muestra la publicación real, en vivo)
--   GET /api/ordenes           → [] (vacío), para que la creación de la
--     orden en BORRADOR se vea también en vivo durante la grabación
-- ============================================================
