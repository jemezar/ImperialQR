-- ===================================================
-- Migración V1: Creación del esquema inicial
-- Sistema Imperial QR - Restaurante Imperial
-- ===================================================

-- 1. Tabla: usuario
CREATE TABLE IF NOT EXISTS usuario (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    rol VARCHAR(30) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 2. Tabla: mesa
CREATE TABLE IF NOT EXISTS mesa (
    id BIGSERIAL PRIMARY KEY,
    numero INTEGER NOT NULL UNIQUE,
    capacidad INTEGER NOT NULL DEFAULT 4,
    codigo_qr VARCHAR(64) NOT NULL UNIQUE,
    estado VARCHAR(30) NOT NULL DEFAULT 'DISPONIBLE'
);

-- 3. Tabla: categoria
CREATE TABLE IF NOT EXISTS categoria (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    descripcion VARCHAR(255),
    activo BOOLEAN NOT NULL DEFAULT TRUE
);

-- 4. Tabla: ingrediente
CREATE TABLE IF NOT EXISTS ingrediente (
    id BIGSERIAL PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL UNIQUE,
    unidad VARCHAR(30) DEFAULT 'unidad',
    precio_extra NUMERIC(12, 2) NOT NULL DEFAULT 0.00,
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- 5. Tabla: plato
CREATE TABLE IF NOT EXISTS plato (
    id BIGSERIAL PRIMARY KEY,
    categoria_id BIGINT NOT NULL REFERENCES categoria(id) ON DELETE RESTRICT,
    nombre VARCHAR(150) NOT NULL,
    descripcion VARCHAR(500),
    precio_base NUMERIC(12, 2) NOT NULL,
    imagen_url VARCHAR(255),
    disponible BOOLEAN NOT NULL DEFAULT TRUE
);

-- 6. Tabla: plato_ingrediente
CREATE TABLE IF NOT EXISTS plato_ingrediente (
    plato_id BIGINT NOT NULL REFERENCES plato(id) ON DELETE CASCADE,
    ingrediente_id BIGINT NOT NULL REFERENCES ingrediente(id) ON DELETE RESTRICT,
    cantidad NUMERIC(10, 2) DEFAULT 1.00,
    removible BOOLEAN NOT NULL DEFAULT TRUE,
    adicionable BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (plato_id, ingrediente_id)
);

-- 7. Tabla: orden
CREATE TABLE IF NOT EXISTS orden (
    id BIGSERIAL PRIMARY KEY,
    tipo VARCHAR(20) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    mesa_id BIGINT REFERENCES mesa(id) ON DELETE SET NULL,
    cliente_id BIGINT,
    apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    cierre TIMESTAMP,
    subtotal NUMERIC(12, 2) DEFAULT 0.00,
    impuesto NUMERIC(12, 2) DEFAULT 0.00,
    propina NUMERIC(12, 2) DEFAULT 0.00,
    costo_envio NUMERIC(12, 2) DEFAULT 0.00,
    total NUMERIC(12, 2) DEFAULT 0.00
);

-- Índice parcial: Solo una orden ABIERTA por mesa (RN-01)
CREATE UNIQUE INDEX IF NOT EXISTS uq_orden_mesa_abierta ON orden(mesa_id) WHERE (estado = 'ABIERTA');

-- 8. Tabla: detalle_orden
CREATE TABLE IF NOT EXISTS detalle_orden (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES orden(id) ON DELETE CASCADE,
    plato_id BIGINT NOT NULL REFERENCES plato(id) ON DELETE RESTRICT,
    cantidad INTEGER NOT NULL DEFAULT 1,
    precio_unitario NUMERIC(12, 2) NOT NULL,
    estado VARCHAR(30) NOT NULL DEFAULT 'RECIBIDO',
    observaciones VARCHAR(255),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    en_preparacion_en TIMESTAMP,
    listo_en TIMESTAMP,
    entregado_en TIMESTAMP,
    cocinero_id BIGINT,
    mesero_id BIGINT
);

-- 9. Tabla: modificacion_detalle
CREATE TABLE IF NOT EXISTS modificacion_detalle (
    id BIGSERIAL PRIMARY KEY,
    detalle_id BIGINT NOT NULL REFERENCES detalle_orden(id) ON DELETE CASCADE,
    ingrediente_id BIGINT NOT NULL REFERENCES ingrediente(id) ON DELETE RESTRICT,
    accion VARCHAR(20) NOT NULL,
    costo_extra NUMERIC(12, 2) NOT NULL DEFAULT 0.00
);

-- 10. Tabla: pago
CREATE TABLE IF NOT EXISTS pago (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL REFERENCES orden(id) ON DELETE CASCADE,
    metodo VARCHAR(30) NOT NULL,
    monto NUMERIC(12, 2) NOT NULL,
    referencia VARCHAR(100),
    estado VARCHAR(30) NOT NULL DEFAULT 'APROBADO',
    fecha TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 11. Tabla: encuesta
CREATE TABLE IF NOT EXISTS encuesta (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL UNIQUE REFERENCES orden(id) ON DELETE CASCADE,
    cal_comida INTEGER NOT NULL,
    cal_servicio INTEGER NOT NULL,
    cal_general INTEGER NOT NULL,
    comentario VARCHAR(500),
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 12. Tabla: domicilio
CREATE TABLE IF NOT EXISTS domicilio (
    id BIGSERIAL PRIMARY KEY,
    orden_id BIGINT NOT NULL UNIQUE REFERENCES orden(id) ON DELETE CASCADE,
    nombre_cliente VARCHAR(100) NOT NULL,
    telefono VARCHAR(30) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    notas_direccion VARCHAR(255),
    costo_envio NUMERIC(12, 2) NOT NULL DEFAULT 5000.00,
    estado VARCHAR(30) NOT NULL DEFAULT 'SOLICITADO',
    domiciliario_id BIGINT REFERENCES usuario(id) ON DELETE SET NULL,
    creado_en TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);
