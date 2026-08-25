-- 1. Tabla de Usuarios
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    nombre_completo VARCHAR(100),
    rol VARCHAR(20) NOT NULL -- 'ROLE_ADMIN' o 'ROLE_EMPLEADO'
);

-- 2. Tabla de Bodegas
CREATE TABLE bodegas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    ubicacion VARCHAR(255)
);

-- 3. Tabla de Productos
CREATE TABLE productos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255),
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0
);

-- 4. Tabla de Movimientos (Coordinada con el esquema exacto de tu BD)
CREATE TABLE movimientos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL, -- 'ENTRADA', 'SALIDA', 'TRASLADO'
    cantidad INT NOT NULL,
    fecha_hora TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    producto_id BIGINT NOT NULL,
    bodega_origen_id BIGINT,   -- Puede ser NULL si es una ENTRADA inicial
    bodega_destino_id BIGINT,  -- Puede ser NULL si es una SALIDA
    usuario_id BIGINT NOT NULL,
    CONSTRAINT fk_movimiento_producto FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    CONSTRAINT fk_movimiento_bodega_origen FOREIGN KEY (bodega_origen_id) REFERENCES bodegas(id) ON DELETE SET NULL,
    CONSTRAINT fk_movimiento_bodega_destino FOREIGN KEY (bodega_destino_id) REFERENCES bodegas(id) ON DELETE SET NULL,
    CONSTRAINT fk_movimiento_usuario FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE
);

-- 5. Auditorias

CREATE TABLE auditorias (
    id BIGSERIAL PRIMARY KEY,
    entidad_afectada VARCHAR(255),
    id_entidad VARCHAR(255),
    operacion VARCHAR(255),
    usuario VARCHAR(255),
    fecha TIMESTAMP
);
