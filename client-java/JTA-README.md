# Configuración JTA para Transacciones Distribuidas

## Arquitectura Implementada

Este proyecto implementa **JTA (Java Transaction API)** con **Atomikos** para manejar transacciones distribuidas entre tres bases de datos MySQL independientes:

- **Inventario** (Puerto 3306): Gestión de productos y stock
- **Facturación** (Puerto 3307): Clientes, facturas y líneas de detalle  
- **Pagos** (Puerto 3308): Métodos de pago y transacciones

## Componentes Clave

### 1. Dependencias JTA (pom.xml)
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-jta-atomikos</artifactId>
</dependency>
```

### 2. Configuración de DataSources XA
- `DataSourceConfig.java`: Configura 3 datasources XA con Atomikos
- Cada datasource tiene su propio `uniqueResourceName` para JTA
- Configuración de pools de conexión independientes

### 3. EntityManagerFactory por Base
- `InventarioJpaConfig.java`: Entidades en `cliente.application.models.inventario`
- `FacturacionJpaConfig.java`: Entidades en `cliente.application.models.facturacion`  
- `PagosJpaConfig.java`: Entidades en `cliente.application.models.pagos`

### 4. Transaction Manager
- Atomikos gestiona automáticamente el 2PC (Two-Phase Commit)
- Configuración JTA habilitada en todas las EntityManagerFactory

## Modelos de Datos

### Inventario
```sql
categorias(id, nombre, descripcion)
items(id, sku, nombre, categoria_id, stock)
```

### Facturación  
```sql
clientes(id, nombre, email, nif)
facturas(id, numero, fecha, cliente_id, total)
factura_detalle(id, factura_id, concepto, cantidad, precio_unitario)
```

### Pagos
```sql
metodos_pago(id, codigo, descripcion) 
pagos(id, referencia, fecha, importe, moneda, metodo_id, estado)
```

## Uso de Transacciones Distribuidas

### Ejemplo Básico
```java
@Service
@Transactional(rollbackFor = Exception.class)
public class VentaService {
    
    public void procesarVentaCompleta(String sku, Integer cantidad, Long clienteId) {
        // 1. Actualizar stock (BD Inventario)
        inventarioService.reducirStock(sku, cantidad);
        
        // 2. Crear factura (BD Facturación)  
        Long facturaId = facturacionService.crearFactura(clienteId, sku, cantidad);
        
        // 3. Procesar pago (BD Pagos)
        pagosService.procesarPago(facturaId, calcularTotal(sku, cantidad));
        
        // Si cualquier operación falla, TODAS se revierten automáticamente
    }
}
```

### Ventajas del Approach JTA

1. **Consistencia ACID**: Garantiza que todas las operaciones se completen o todas se reviertan
2. **Transacciones Atómicas**: Una falla en cualquier base revierte todas las operaciones
3. **Aislamiento**: Las transacciones no se interfieren entre sí
4. **Durabilidad**: Los cambios son persistentes solo cuando toda la transacción commit

## Comandos de Despliegue

### 1. Levantar las Bases de Datos
```bash
cd infra
docker compose up -d
```

### 2. Verificar Conexiones
```bash
# Inventario (puerto 3306)
docker exec -it mysql-inventario mysql -uequipo -p inventario -e "SELECT COUNT(*) FROM items;"

# Facturación (puerto 3307)  
docker exec -it mysql-facturacion mysql -uequipo -p facturacion -e "SELECT COUNT(*) FROM facturas;"

# Pagos (puerto 3308)
docker exec -it mysql-pagos mysql -uequipo -p pagos -e "SELECT COUNT(*) FROM pagos;"
```

### 3. Ejecutar Aplicación
```bash
cd client-java
mvn spring-boot:run
```

## Monitoreo de Transacciones

### Logs de Atomikos
- Los logs de Atomikos se escriben automáticamente
- Incluyen información de 2PC y recovery
- Configurables en `application.properties`

### Verificación de Consistencia
```java
// Si una transacción falla a medias, verificar que NO hay cambios parciales:
@Test  
public void testTransactionRollback() {
    // Simular falla en paso 2 de 3
    // Verificar que paso 1 también se revirtió
}
```

## Configuración Avanzada

### Timeouts y Recovery
```properties
# En application.properties
spring.jta.atomikos.properties.default-jta-timeout=300000
spring.jta.atomikos.properties.max-timeout=300000
spring.jta.atomikos.properties.enable-logging=true
```

### Pool de Conexiones por Base
```java
// En DataSourceConfig.java
dataSource.setMinPoolSize(3);      // Mínimo de conexiones
dataSource.setMaxPoolSize(25);     // Máximo de conexiones  
dataSource.setMaxLifetime(20000);  // Tiempo de vida máximo
dataSource.setBorrowConnectionTimeout(30); // Timeout para obtener conexión
```

## Troubleshooting

### Error: "No transaction manager found"
- Verificar que `@EnableTransactionManagement` esté presente
- Comprobar que Atomikos esté en el classpath

### Error: "XA resource not found"
- Verificar `uniqueResourceName` en cada datasource
- Comprobar conectividad a cada puerto MySQL

### Error: "Transaction timeout"
- Aumentar timeouts en configuración Atomikos
- Optimizar consultas lentas

### Rollback Parcial
- Verificar que todas las operaciones usen `@Transactional`
- Comprobar que no hay commits manuales intermedios

## Migración desde Configuración Simple

Si tienes código existente con una sola base:

1. **Mantener modelos originales** para compatibilidad temporal
2. **Crear nuevos modelos** en packages específicos por dominio  
3. **Migrar servicios gradualmente** a usar múltiples repositories
4. **Probar exhaustivamente** las transacciones distribuidas
5. **Documentar dependencias** entre dominios

La configuración JTA convive con el modelo original, permitiendo migración gradual.