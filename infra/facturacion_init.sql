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
 ('Empresa Uno','contacto@empresa1.com','A1234567'),
 ('Empresa Dos','info@empresa2.com','B7654321')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO facturas (numero, fecha, cliente_id, total) VALUES
 ('F-2025-0001', CURRENT_DATE(), 1, 0),
 ('F-2025-0002', CURRENT_DATE(), 2, 0)
ON DUPLICATE KEY UPDATE fecha=VALUES(fecha);

INSERT INTO factura_detalle (factura_id, concepto, cantidad, precio_unitario) VALUES
 (1,'Servicio consultoría',10,150.00),
 (1,'Licencia software',5,200.00),
 (2,'Mantenimiento anual',1,1200.00)
ON DUPLICATE KEY UPDATE concepto=VALUES(concepto), cantidad=VALUES(cantidad), precio_unitario=VALUES(precio_unitario);

-- Recalcular totales
UPDATE facturas f SET total = (
    SELECT COALESCE(SUM(cantidad*precio_unitario),0) FROM factura_detalle d WHERE d.factura_id = f.id
);

SELECT 'Clientes' label, COUNT(*) total FROM clientes UNION ALL
SELECT 'Facturas', COUNT(*) FROM facturas UNION ALL
SELECT 'Lineas', COUNT(*) FROM factura_detalle;
