-- Script completo de inicialización de la base de datos
-- Este script crea las tablas y carga los datos de prueba

USE productos;

-- Crear tablas si no existen
CREATE TABLE IF NOT EXISTS organizations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL
);

CREATE TABLE IF NOT EXISTS categories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    description VARCHAR(512)
);

CREATE TABLE IF NOT EXISTS products (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    organization_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    FOREIGN KEY (organization_id) REFERENCES organizations(id),
    FOREIGN KEY (category_id) REFERENCES categories(id)
);

-- Limpiar datos existentes (opcional)
DELETE FROM products;
DELETE FROM categories;
DELETE FROM organizations;

-- Insertar organizaciones
INSERT INTO organizations (name) VALUES 
('TechCorp Solutions'),
('InnovateLab'),
('Digital Dynamics'),
('Future Systems'),
('CloudTech Inc');

-- Insertar categorías
INSERT INTO categories (name, description) VALUES 
('Software', 'Aplicaciones y programas informaticos'),
('Hardware', 'Componentes fisicos de computacion'),
('Servicios', 'Servicios de consultoria y soporte'),
('Cloud', 'Soluciones de computacion en la nube'),
('Mobile', 'Aplicaciones y servicios moviles'),
('AI/ML', 'Inteligencia artificial y machine learning'),
('Security', 'Soluciones de seguridad informatica'),
('Data', 'Analisis y gestion de datos');

-- Insertar productos
INSERT INTO products (name, organization_id, category_id) VALUES 
-- TechCorp Solutions
('ERP Enterprise Suite', 1, 1),
('Cloud Infrastructure Manager', 1, 4),
('AI Analytics Platform', 1, 6),
('Mobile Banking App', 1, 5),

-- InnovateLab
('IoT Sensor Network', 2, 2),
('Machine Learning API', 2, 6),
('Blockchain Security Suite', 2, 7),
('Real-time Data Processor', 2, 8),

-- Digital Dynamics
('E-commerce Platform', 3, 1),
('Customer Analytics Dashboard', 3, 8),
('Payment Gateway Service', 3, 3),
('Mobile POS System', 3, 5),

-- Future Systems
('Quantum Computing Simulator', 4, 1),
('Advanced Robotics Controller', 4, 2),
('Predictive Maintenance System', 4, 6),
('Smart City Management Platform', 4, 4),

-- CloudTech Inc
('Multi-Cloud Orchestrator', 5, 4),
('Container Security Scanner', 5, 7),
('Serverless Function Platform', 5, 4),
('Database Migration Tool', 5, 8),
('API Gateway Service', 5, 3),
('Monitoring & Alerting System', 5, 3);

-- Verificar datos insertados
SELECT 'Organizations:' as info, COUNT(*) as count FROM organizations
UNION ALL
SELECT 'Categories:', COUNT(*) FROM categories
UNION ALL
SELECT 'Products:', COUNT(*) FROM products;
