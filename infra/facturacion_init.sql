-- Inicialización base de datos FACTURACION
-- Objetivo: tablas de clientes, facturas y detalle

USE facturacion;

CREATE TABLE IF NOT EXISTS clientes (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(180) NOT NULL,
    email VARCHAR(180) UNIQUE,
    nif VARCHAR(32) UNIQUE
);

CREATE TABLE IF NOT EXISTS facturas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    numero VARCHAR(40) NOT NULL UNIQUE,
    fecha DATE NOT NULL,
    cliente_id BIGINT NOT NULL,
    total DECIMAL(12,2) NOT NULL DEFAULT 0,
    FOREIGN KEY (cliente_id) REFERENCES clientes(id)
);

CREATE TABLE IF NOT EXISTS factura_detalle (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    factura_id BIGINT NOT NULL,
    concepto VARCHAR(255) NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (factura_id) REFERENCES facturas(id)
);

-- Datos iniciales
INSERT INTO clientes (nombre, email, nif) VALUES
 ('Juan Pérez García','juan.perez@example.com','12345678A'),
 ('María López Martínez','maria.lopez@example.com','23456789B'),
 ('Carlos Rodríguez Sánchez','carlos.rodriguez@example.com','34567890C'),
 ('Ana Fernández González','ana.fernandez@example.com','45678901D'),
 ('Luis Martínez Torres','luis.martinez@example.com','56789012E')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

SELECT 'Clientes' label, COUNT(*) total FROM clientes;
