-- Inicialización base de datos USUARIOS
-- Objetivo: gestionar usuarios con datos personales y financieros

USE usuarios;

-- Tabla principal de usuarios
CREATE TABLE IF NOT EXISTS usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    fecha_registro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    ultima_sesion TIMESTAMP NULL,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de datos personales
CREATE TABLE IF NOT EXISTS datos_personales (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    nombre VARCHAR(100) NOT NULL,
    apellido VARCHAR(100) NOT NULL,
    fecha_nacimiento DATE NULL,
    telefono VARCHAR(20) NULL,
    direccion VARCHAR(255) NULL,
    ciudad VARCHAR(100) NULL,
    pais VARCHAR(100) NULL,
    codigo_postal VARCHAR(20) NULL,
    documento_identidad VARCHAR(50) NULL UNIQUE,
    tipo_documento ENUM('CARNET', 'PASAPORTE', 'CEDULA', 'OTRO') DEFAULT 'CEDULA',
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_documento (documento_identidad),
    INDEX idx_nombre_apellido (nombre, apellido)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Tabla de datos financieros
CREATE TABLE IF NOT EXISTS datos_financieros (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE,
    numero_cuenta VARCHAR(34) NULL COMMENT 'Número de cuenta bancaria (IBAN)',
    tipo_cuenta ENUM('AHORROS', 'CORRIENTE', 'NOMINA', 'EMPRESARIAL') DEFAULT 'AHORROS',
    banco VARCHAR(100) NULL,
    titular_cuenta VARCHAR(200) NULL,
    tarjeta_credito VARCHAR(19) NULL COMMENT 'Últimos 4 dígitos o enmascarado',
    limite_credito DECIMAL(15,2) NULL DEFAULT 0.00,
    saldo_disponible DECIMAL(15,2) NULL DEFAULT 0.00,
    moneda VARCHAR(3) DEFAULT 'USD' COMMENT 'Código ISO 4217',
    verificado BOOLEAN DEFAULT FALSE,
    fecha_verificacion TIMESTAMP NULL,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_verificado (verificado)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Datos iniciales de ejemplo
INSERT INTO usuarios (username, email, password_hash, activo) VALUES
 ('admin', 'admin@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
 ('jdoe', 'john.doe@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
 ('msmith', 'mary.smith@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', TRUE),
 ('rjohnson', 'robert.johnson@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', FALSE)
ON DUPLICATE KEY UPDATE email=VALUES(email);

INSERT INTO datos_personales (usuario_id, nombre, apellido, fecha_nacimiento, telefono, direccion, ciudad, pais, codigo_postal, documento_identidad, tipo_documento) VALUES
 (1, 'Admin', 'System', '1990-01-01', '+1-555-0001', '123 Admin St', 'New York', 'USA', '10001', 'ADM001', 'PASAPORTE'),
 (2, 'John', 'Doe', '1985-05-15', '+1-555-0002', '456 Main St', 'Los Angeles', 'USA', '90001', 'DNI12345678', 'CEDULA'),
 (3, 'Mary', 'Smith', '1992-08-20', '+1-555-0003', '789 Oak Ave', 'Chicago', 'USA', '60601', 'DNI87654321', 'CEDULA'),
 (4, 'Robert', 'Johnson', '1988-03-10', '+1-555-0004', '321 Pine Rd', 'Houston', 'USA', '77001', 'PAS9876543', 'PASAPORTE')
ON DUPLICATE KEY UPDATE nombre=VALUES(nombre);

INSERT INTO datos_financieros (usuario_id, numero_cuenta, tipo_cuenta, banco, titular_cuenta, tarjeta_credito, limite_credito, saldo_disponible, moneda, verificado) VALUES
 (1, 'US12345678901234567890', 'EMPRESARIAL', 'Bank of America', 'Admin System', '**** **** **** 1001', 50000.00, 45000.00, 'USD', TRUE),
 (2, 'US98765432109876543210', 'AHORROS', 'Chase Bank', 'John Doe', '**** **** **** 2002', 10000.00, 8500.00, 'USD', TRUE),
 (3, 'US11223344556677889900', 'CORRIENTE', 'Wells Fargo', 'Mary Smith', '**** **** **** 3003', 15000.00, 12000.00, 'USD', TRUE),
 (4, 'US99887766554433221100', 'NOMINA', 'Citibank', 'Robert Johnson', NULL, 5000.00, 4800.00, 'USD', FALSE)
ON DUPLICATE KEY UPDATE banco=VALUES(banco);

-- Resumen de datos inicializados
SELECT 'Usuarios registrados' AS label, COUNT(*) AS total FROM usuarios
UNION ALL
SELECT 'Datos personales', COUNT(*) FROM datos_personales
UNION ALL
SELECT 'Datos financieros', COUNT(*) FROM datos_financieros;
