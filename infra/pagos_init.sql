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
 ('TARJETA','Tarjeta de Crédito/Débito'),
 ('TRANSFERENCIA','Transferencia Bancaria'),
 ('PAYPAL','PayPal'),
 ('EFECTIVO','Pago en Efectivo'),
 ('BIZUM','Bizum')
ON DUPLICATE KEY UPDATE descripcion=VALUES(descripcion);

SELECT 'Metodos' label, COUNT(*) total FROM metodos_pago UNION ALL
SELECT 'Pagos', COUNT(*) FROM pagos;
