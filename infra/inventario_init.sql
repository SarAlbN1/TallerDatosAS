-- Inicialización base de datos INVENTARIO
-- Objetivo: tablas para items y stock

USE inventario;

CREATE TABLE IF NOT EXISTS categorias (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL UNIQUE,
    descripcion VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    sku VARCHAR(64) NOT NULL UNIQUE,
    nombre VARCHAR(160) NOT NULL,
    categoria_id BIGINT,
    stock INT NOT NULL DEFAULT 0,
    FOREIGN KEY (categoria_id) REFERENCES categorias(id)
);

-- Datos iniciales
INSERT INTO categorias (nombre, descripcion) VALUES
 ('Hardware','Componentes físicos'),
 ('Accesorios','Periféricos y complementos'),
 ('Software','Licencias y paquetes')
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

INSERT INTO items (sku, nombre, categoria_id, stock) VALUES
 ('HW-001','Disco SSD 1TB',1,25),
 ('HW-002','Memoria 16GB DDR4',1,40),
 ('AC-001','Teclado mecánico',2,15),
 ('SW-001','Licencia Antivirus',3,100)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), stock=VALUES(stock);

SELECT 'Inventario categorias' label, COUNT(*) total FROM categorias UNION ALL
SELECT 'Inventario items', COUNT(*) FROM items;
