# Testing Distributed Transactions with JTA

Esta guía explica cómo probar las transacciones distribuidas implementadas con JTA (Java Transaction API) y Atomikos en el proyecto.

## Configuración Previa

1. **Iniciar las bases de datos:**
   ```bash
   cd infra
   docker-compose up -d
   ```

2. **Iniciar la aplicación Spring Boot:**
   ```bash
   cd client-java
   mvn spring-boot:run
   ```

## Endpoints de Prueba

### 1. Health Check
Verificar que el servicio está funcionando:
```bash
curl http://localhost:8080/api/transactions/health
```

### 2. Transacción Distribuida Exitosa
Probar una transacción que abarca las tres bases de datos:
```bash
curl -X POST "http://localhost:8080/api/transactions/venta-completa" \
  -d "sku=LAPTOP001" \
  -d "cantidad=2" \
  -d "clienteId=1" \
  -d "metodoPagoId=TARJETA"
```

### 3. Transacción con Rollback
Probar que el rollback funciona correctamente:
```bash
curl -X POST "http://localhost:8080/api/transactions/simular-fallo" \
  -d "sku=LAPTOP001" \
  -d "cantidad=1" \
  -d "clienteId=1" \
  -d "metodoPagoId=EFECTIVO"
```

### 4. Transacciones Individuales

#### Actualizar Stock (Solo Inventario)
```bash
curl -X POST "http://localhost:8080/api/transactions/actualizar-stock" \
  -d "sku=MOUSE001" \
  -d "cantidad=5"
```

#### Crear Factura (Solo Facturación)
```bash
curl -X POST "http://localhost:8080/api/transactions/crear-factura" \
  -d "clienteId=1" \
  -d "sku=MOUSE001" \
  -d "cantidad=5" \
  -d "total=250.00"
```

#### Procesar Pago (Solo Pagos)
```bash
curl -X POST "http://localhost:8080/api/transactions/procesar-pago" \
  -d "metodoPagoId=TARJETA" \
  -d "monto=250.00"
```

## Verificación de Resultados

### Monitorear Logs
Los logs de la aplicación mostrarán:
- Inicio y finalización de transacciones
- Operaciones en cada base de datos
- Errores y rollbacks

### Verificar Datos en las Bases
Conectarse a las bases de datos para verificar los cambios:

```bash
# Base Inventario (Puerto 3306)
docker exec -it inventario-db mysql -u root -p inventario
SELECT * FROM categorias_inventario;
SELECT * FROM items;

# Base Facturación (Puerto 3307) 
docker exec -it facturacion-db mysql -u root -p facturacion
SELECT * FROM clientes;
SELECT * FROM facturas;
SELECT * FROM factura_detalles;

# Base Pagos (Puerto 3308)
docker exec -it pagos-db mysql -u root -p pagos
SELECT * FROM metodos_pago;
SELECT * FROM pagos;
```

## Escenarios de Prueba

### 1. Transacción Exitosa
- **Qué sucede:** Se actualiza el stock, se crea la factura y se procesa el pago
- **Resultado esperado:** Datos guardados en las tres bases de datos
- **Verificación:** Los registros aparecen en todas las tablas correspondientes

### 2. Transacción con Fallo
- **Qué sucede:** Se actualiza el stock, se crea la factura, pero falla antes del pago
- **Resultado esperado:** Rollback completo - ningún dato se guarda
- **Verificación:** No aparecen registros nuevos en ninguna base de datos

### 3. Transacciones Individuales
- **Qué sucede:** Operaciones aisladas en una sola base de datos
- **Resultado esperado:** Solo se afecta la base de datos correspondiente
- **Verificación:** Los cambios aparecen solo en la base específica

## Configuración JTA

El proyecto utiliza:
- **Atomikos:** Como gestor de transacciones JTA
- **XA DataSources:** Para soporte de two-phase commit
- **Multiple EntityManagerFactory:** Configuración separada por dominio

### Archivos Clave:
- `DataSourceConfig.java`: Configuración de datasources XA
- `InventarioJpaConfig.java`: Configuración JPA para inventario
- `FacturacionJpaConfig.java`: Configuración JPA para facturación  
- `PagosJpaConfig.java`: Configuración JPA para pagos
- `TransactionDistribuidaService.java`: Lógica de negocio con transacciones distribuidas

## Troubleshooting

### Error de Conexión a Base de Datos
```
Solution: Verificar que los contenedores estén corriendo con docker ps
```

### Error de Transacción
```
Solution: Revisar logs de Atomikos y verificar configuración XA
```

### Rollback No Funciona
```
Solution: Verificar que @Transactional incluya rollbackFor = Exception.class
```