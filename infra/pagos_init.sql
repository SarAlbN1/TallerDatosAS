-- Inicialización base de datos PAGOS
-- Objetivo: registrar transacciones y métodos de pago

USE pagos;

CREATE TABLE IF NOT EXISTS metodos_pago (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    codigo VARCHAR(40) NOT NULL UNIQUE,
    descripcion VARCHAR(160)
);

CREATE TABLE IF NOT EXISTS pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    referencia VARCHAR(60) NOT NULL UNIQUE,
    fecha DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    importe DECIMAL(12,2) NOT NULL,
    moneda CHAR(3) NOT NULL DEFAULT 'EUR',
    metodo_id BIGINT,
    estado ENUM('PENDIENTE','CONFIRMADO','FALLIDO','REEMBOLSADO') NOT NULL DEFAULT 'PENDIENTE',
    FOREIGN KEY (metodo_id) REFERENCES metodos_pago(id)
);

-- Datos iniciales
INSERT INTO metodos_pago (codigo, descripcion) VALUES
 ('TARJETA','Pago con tarjeta bancaria'),
 ('TRANSFER','Transferencia bancaria'),
 ('PAYPAL','Pasarela PayPal')
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

INSERT INTO pagos (referencia, importe, moneda, metodo_id, estado) VALUES
 ('TX-10001', 150.00, 'EUR', 1, 'CONFIRMADO'),
 ('TX-10002', 89.99, 'EUR', 2, 'PENDIENTE'),
 ('TX-10003', 15.50, 'EUR', 3, 'FALLIDO')
ON DUPLICATE KEY UPDATE importe=VALUES(importe), estado=VALUES(estado);

SELECT 'Metodos' label, COUNT(*) total FROM metodos_pago UNION ALL
SELECT 'Pagos', COUNT(*) FROM pagos;
