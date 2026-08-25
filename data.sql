-- 1. Insertar Usuarios (Contraseña cifrada con BCrypt para '12345')
INSERT INTO usuarios (username, password, nombre_completo, rol) VALUES
('admin1', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1FX.25V07h5F5rUnG.K33M89I15q36S', 'Carlos Administrador', 'ROLE_ADMIN'),
('empleado1', '$2a$10$e0MYzXyjpJS7Pd0RVvHwHe1FX.25V07h5F5rUnG.K33M89I15q36S', 'Juan Empleado', 'ROLE_EMPLEADO');

-- 2. Insertar Bodegas Iniciales (Con capacidad y encargado)
INSERT INTO bodega (nombre, ubicacion, capacidad, encargado) VALUES
('Bodega Principal (Central)', 'Zona Franca - Módulo 1', 5000, 'Pedro Gómez'),
('Bodega Secundaria (Norte)', 'Parque Industrial - Módulo 4', 2500, 'María Rodríguez');

-- 3. Insertar Productos Iniciales (Asignados a su bodega_id)
INSERT INTO productos (nombre, descripcion, precio, stock, bodega_id) VALUES
('Arroz Roa 1kg', 'Bolsa de arroz blanco 1kg', 4500.00, 150, 1),
('Aceite Gourmet 1L', 'Botella de aceite vegetal 1000ml', 18500.00, 80, 2),
('Azúcar Incauca 1kg', 'Bolsa de azúcar refinada', 3800.00, 200, 1);

-- 4. Insertar Movimientos de prueba (Julio de 2026)
-- Referencian a producto_id (1,2,3), bodega_id (1,2) y usuario_id (1,2)
INSERT INTO movimientos (tipo, cantidad, fecha_hora, producto_id, bodega_origen_id, bodega_destino_id, usuario_id) VALUES
-- Movimiento 1: ENTRADA inicial de Arroz (producto 1) a Bodega Principal (1) realizada por admin1 (usuario 1)
('ENTRADA', 100, '2026-07-01 08:30:00', 1, NULL, 1, 1),

-- Movimiento 2: SALIDA de Arroz (producto 1) desde Bodega Principal (1) realizada por empleado1 (usuario 2)
('SALIDA', 10, '2026-07-10 14:15:00', 1, 1, NULL, 2),

-- Movimiento 3: ENTRADA de Aceite (producto 2) a Bodega Secundaria (2) realizada por admin1 (usuario 1)
('ENTRADA', 50, '2026-07-12 11:00:00', 2, NULL, 2, 1),

-- Movimiento 4: TRASLADO de Aceite (producto 2) de Bodega 1 a Bodega 2 realizada por empleado1 (usuario 2)
('TRASLADO', 15, '2026-07-15 16:45:00', 2, 1, 2, 2),

-- Movimiento 5: ENTRADA de Azúcar (producto 3) a Bodega Principal (1) realizada por admin1 (usuario 1)
('ENTRADA', 200, '2026-07-20 09:00:00', 3, NULL, 1, 1),

-- Movimiento 6: SALIDA de Azúcar (producto 3) desde Bodega Secundaria (2) realizada por empleado1 (usuario 2)
('SALIDA', 20, '2026-07-23 10:30:00', 3, 2, NULL, 2);  


