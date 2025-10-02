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
 ('Electrónica','Productos electrónicos y accesorios'),
 ('Oficina','Artículos de oficina y papelería'),
 ('Computación','Equipos y accesorios de computación')
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

INSERT INTO items (sku, nombre, categoria_id, stock) VALUES
 ('LAPTOP001','Laptop Dell Inspiron 15',3,10),
 ('LAPTOP002','Laptop HP Pavilion',3,15),
 ('MOUSE001','Mouse Logitech MX Master 3',3,50),
 ('KEYBOARD001','Teclado Mecánico Corsair K95',3,30),
 ('MONITOR001','Monitor Samsung 27" 4K',1,20),
 ('HEADSET001','Audífonos Sony WH-1000XM4',1,25),
 ('WEBCAM001','Webcam Logitech C920',1,40),
 ('PRINTER001','Impresora HP LaserJet Pro',2,12),
 ('DESK001','Escritorio Ajustable Eléctrico',2,8),
 ('CHAIR001','Silla Ergonómica Herman Miller',2,15)
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre), stock=VALUES(stock);

SELECT 'Inventario categorias' label, COUNT(*) total FROM categorias UNION ALL
SELECT 'Inventario items', COUNT(*) FROM items;
