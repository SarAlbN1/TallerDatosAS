# TallerDatosAS - Sistema Distribuido con Transacciones JTA, Kafka y Notificaciones por Email

Este proyecto implementa un sistema completo de gestión de productos con arquitectura distribuida, transacciones ACID, notificaciones asíncronas con Kafka y sistema de notificaciones por email, cumpliendo todos los requisitos del taller:

- **Punto 1**: Bases de datos distribuidas con Docker (5 instancias MySQL + Kafka)
- **Punto 2**: Cliente pesado Java con JPA, JTA y Kafka (Java Transaction API)
- **Punto 3**: Aplicación Web SPA (React) con carrito y checkout
- **Punto 4**: Arquitectura de dos niveles con servicios REST/SOAP/gRPC
- **Punto 5**: Sistema de notificaciones Kafka con proveedores distribuidos
- **Punto 6**: Sistema de notificaciones por email con Mailtrap

## 🏗️ Arquitectura del Sistema

```
TallerDatosAS/
├── infra/                    # Docker + MySQL + Kafka + Proveedores
├── client-java/             # Backend Spring Boot + JTA + JPA + REST/SOAP/gRPC + Kafka
├── frontend-react/          # Frontend React SPA moderno con carrito
├── frontend-mpa/            # Frontend MPA para arquitectura de dos niveles
├── proveedor-a-app/         # Microservicio Proveedor A (Tecnología)
├── proveedor-b-app/         # Microservicio Proveedor B (Periféricos)
├── proveedor-c-app/         # Microservicio Proveedor C (Otros)
└── README.md
```

## 🌐 Bases de Datos Distribuidas y Kafka

El sistema utiliza **cinco bases de datos independientes** más **Kafka** para separar los dominios de negocio y notificaciones:

| Base | Contenedor | Puerto Host | Script Init | Objetivo |
|------|------------|------------|-------------|----------|
| **inventario** | mysql-inventario | 3306 | `inventario_init.sql` | Gestión de stock y artículos |
| **facturacion** | mysql-facturacion | 3307 | `facturacion_init.sql` | Clientes, facturas y líneas |
| **pagos** | mysql-pagos | 3308 | `pagos_init.sql` | Métodos y transacciones de pago |
| **usuarios** | mysql-usuarios | 3309 | `usuarios_init.sql` | Datos de usuarios y clientes |
| **productos** | mysql-productos | 3310 | `productos_init.sql` | Catálogo de productos |
| **Kafka** | kafka + zookeeper | 9092 | - | Mensajería asíncrona |

### Características Técnicas:
- **MySQL 8.4 LTS** con soporte XA para transacciones distribuidas
- **Apache Kafka** con Zookeeper para notificaciones asíncronas
- **JTA (Java Transaction API)** con Atomikos como transaction manager
- **Two-Phase Commit** para garantizar consistencia ACID entre bases
- **Configuración XA DataSource** separada para cada dominio
- **3 Microservicios de Proveedores** con Kafka consumers

## 🚀 Instrucciones de Despliegue

### Prerrequisitos
- **Java 17** (OpenJDK recomendado) - **IMPORTANTE: Usar Java 17, no Java 21**
- **Node.js 18+** (para el frontend React)
- **Docker y Docker Compose** (para las bases de datos y Kafka)
- **Maven** (para el backend Java)

### 1. Configurar Java (macOS con Homebrew)
```bash
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
java -version  # Debe mostrar OpenJDK 17.0.16
```

### 2. Levantar la Infraestructura Completa (Bases de Datos + Kafka + Proveedores)
```bash
cd infra
docker compose up -d
```

**Esto levanta automáticamente:**
- 5 bases de datos MySQL (puertos 3306-3310)
- Kafka + Zookeeper (puerto 9092)
- 3 microservicios de proveedores (proveedor-a, proveedor-b, proveedor-c)

Credenciales (idénticas en todos los contenedores MySQL):
- Usuario app: `equipo`
- Contraseña: `123456`
- Root: definido en `compose.yaml` (`MYSQL_ROOT_PASSWORD`)

Cada script se ejecuta SOLO la primera vez (volumen vacío). Para re-ejecutar, elimina los volúmenes:
```bash
docker compose down
docker volume rm $(docker volume ls -q | grep -E "inventario|facturacion|pagos|usuarios|productos")
docker compose up -d
```

### 3. Ejecutar el Backend Principal
#### Windows (PowerShell)
```powershell
cd client-java
mvn spring-boot:run
```

#### macOS / Linux (bash/zsh)
```bash
cd client-java
export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
./mvnw spring-boot:run
```

**IMPORTANTE:** Usar `./mvnw` (Maven Wrapper) en lugar de `mvn` para evitar problemas de versión.

### 4. Ejecutar el Frontend SPA (React)
```bash
cd frontend-react
npm install
npm run dev
```

### 5. Ejecutar el Frontend MPA (Arquitectura de Dos Niveles)

#### Opción A: Con Servidor Python (Recomendado)
```bash
cd frontend-mpa
python server.py
```

#### Opción B: Con Servidor Python en Puerto Personalizado
```bash
cd frontend-mpa
python server.py 8081  # Especificar puerto personalizado
```

#### Opción C: Abrir Directamente (Solo para pruebas básicas)
```bash
# Abrir directamente el archivo HTML
open frontend-mpa/index.html  # macOS
start frontend-mpa/index.html # Windows
xdg-open frontend-mpa/index.html # Linux
```

**Nota importante**: Para que funcione correctamente con los endpoints REST y SOAP, se recomienda usar el servidor Python que incluye configuración CORS.

## 🌐 Acceso a la Aplicación

### URLs Principales:
- **Frontend SPA (React)**: http://localhost:3000 (o puerto disponible)
- **Frontend MPA (Arquitectura 2 niveles)**: http://localhost:3001
- **Backend API REST**: http://localhost:8080/api
- **Backend SOAP**: http://localhost:8080/ws
- **Kafka**: localhost:9092
- **BD Inventario**: localhost:3306 (db: inventario)
- **BD Facturación**: localhost:3307 (db: facturacion)
- **BD Pagos**: localhost:3308 (db: pagos)
- **BD Usuarios**: localhost:3309 (db: usuarios)
- **BD Productos**: localhost:3310 (db: productos)

### URLs Específicas del Frontend MPA:
- **Dashboard Principal**: http://localhost:3001/
- **Productos REST**: http://localhost:3001/src/pages/products.html
- **Productos SOAP**: http://localhost:3001/src/pages/soap-products.html
- **Organizaciones**: http://localhost:3001/src/pages/organizations.html
- **Categorías**: http://localhost:3001/src/pages/categories.html

## 🚀 Sistema de Notificaciones Kafka y Email

### **Arquitectura Kafka Implementada:**

El sistema incluye un **sistema completo de notificaciones asíncronas** usando Apache Kafka:

#### **Topics Kafka:**
- `ventas-proveedor-a` - Notificaciones para Proveedor A (Tecnología)
- `ventas-proveedor-b` - Notificaciones para Proveedor B (Periféricos)  
- `ventas-proveedor-c` - Notificaciones para Proveedor C (Otros)
- `notificaciones-clientes` - Notificaciones para clientes (✅ **IMPLEMENTADO**)

#### **Enrutamiento Inteligente:**
El sistema determina automáticamente qué proveedor maneja cada producto:
- **Proveedor A**: SKUs que empiezan con `LAPTOP`, `PC`
- **Proveedor B**: SKUs que empiezan con `MOUSE`, `TECLADO`
- **Proveedor C**: Todos los demás SKUs

#### **Flujo de Notificaciones:**
1. **Frontend** → Checkout → **Backend**
2. **Backend** → Procesa venta con JTA → **Kafka Producer**
3. **Kafka Producer** → Envía mensaje al proveedor específico
4. **Proveedor** → Recibe mensaje → Actualiza inventario → Envía notificación
5. **Kafka Producer** → Envía evento a `notificaciones-clientes`
6. **Email Service** → Consume evento → Envía email de confirmación al cliente
7. **Email Service** → Envía notificación al administrador

### **Verificar Sistema Kafka:**
```bash
# Ver logs de proveedores
docker logs proveedor-a --tail 5
docker logs proveedor-b --tail 5  
docker logs proveedor-c --tail 5

# Verificar topics Kafka
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

### **Testing del Sistema:**
```bash
# Probar venta que va al Proveedor A
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{"items": [{"sku": "LAPTOP001", "cantidad": 1}], "clienteId": 1, "metodoPago": "TARJETA"}'

# Probar venta que va al Proveedor B  
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{"items": [{"sku": "MOUSE001", "cantidad": 1}], "clienteId": 2, "metodoPago": "PAYPAL"}'

# Probar venta que va al Proveedor C
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{"items": [{"sku": "MONITOR001", "cantidad": 1}], "clienteId": 3, "metodoPago": "TARJETA"}'

# Probar checkout simple con notificaciones por email
curl -X POST http://localhost:8080/api/checkout/simple \
  -H "Content-Type: application/json" \
  -d '{"productId": 1, "customerName": "Alejandro Test", "customerEmail": "alejandro09pf3@gmail.com", "quantity": 1, "totalPrice": 100.0}'
```

**Resultado esperado:** 
- Solo el proveedor correspondiente debe recibir y procesar cada mensaje
- Los emails se envían automáticamente a Mailtrap para testing

## 📧 Sistema de Notificaciones por Email

### **Configuración de Email**

El sistema utiliza **Mailtrap** para testing de emails en desarrollo:

#### **Credenciales Mailtrap:**
- **Host**: sandbox.smtp.mailtrap.io
- **Puerto**: 2525
- **Usuario**: 19aa0f3606c7fc
- **Contraseña**: cfc52f63310f7b
- **Autenticación**: PLAIN, LOGIN, CRAM-MD5
- **TLS**: Opcional (STARTTLS en todos los puertos)

#### **Configuración en application.properties:**
```properties
# Email Configuration (Mailtrap for testing)
spring.mail.host=sandbox.smtp.mailtrap.io
spring.mail.port=2525
spring.mail.username=${EMAIL_USERNAME:19aa0f3606c7fc}
spring.mail.password=${EMAIL_PASSWORD:cfc52f63310f7b}
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
spring.mail.properties.mail.smtp.starttls.required=true
spring.mail.properties.mail.smtp.ssl.trust=sandbox.smtp.mailtrap.io

# Email Templates
email.from=noreply@tallerdatosas.com
email.from.name=TallerDatosAS
```

### **Flujo de Notificaciones por Email**

1. **Usuario completa compra** en el frontend
2. **Backend procesa checkout** y crea evento de venta
3. **Kafka Producer** envía evento a topic `notificaciones-clientes`
4. **EmailNotificationMDB** consume el evento
5. **EmailService** envía dos emails:
   - **Email de confirmación** al cliente
   - **Email de notificación** al administrador

### **Templates de Email**

#### **Confirmación de Compra (Cliente)**
- **Template**: `templates/email/confirmacion-compra.html`
- **Contenido**: Detalles de la compra, información del producto, total
- **Diseño**: HTML responsive con estilos modernos

#### **Notificación de Nueva Venta (Admin)**
- **Template**: `templates/email/notificacion-admin.html`
- **Contenido**: Resumen de la venta, datos del cliente, productos
- **Diseño**: HTML responsive para administradores

### **Verificar Emails en Mailtrap**

1. **Acceder a Mailtrap**: https://mailtrap.io/
2. **Iniciar sesión** con tu cuenta
3. **Ir a "Inbox de Testing"**
4. **Ver emails enviados** por el sistema
5. **Hacer clic en cualquier email** para ver el contenido HTML

### **Testing del Sistema de Email**

```bash
# Probar checkout con notificaciones por email
curl -X POST http://localhost:8080/api/checkout/simple \
  -H "Content-Type: application/json" \
  -d '{
    "productId": 1,
    "customerName": "Alejandro Test",
    "customerEmail": "alejandro09pf3@gmail.com",
    "quantity": 1,
    "totalPrice": 100.0
  }'
```

**Resultado esperado:**
- Respuesta JSON con detalles de la orden
- Email de confirmación enviado a `alejandro09pf3@gmail.com`
- Email de notificación enviado a `admin@tallerdatosas.com`
- Ambos emails visibles en Mailtrap inbox

### **Frontend - Flujo de Compra con Email**

1. **Abrir frontend MPA**: http://localhost:3001/src/pages/products.html
2. **Hacer clic en cualquier producto** para comprar
3. **Llenar formulario de compra**:
   - Nombre del cliente
   - Email del cliente
   - Cantidad
4. **Hacer clic en "Procesar Compra"**
5. **Ver confirmación** en pantalla
6. **Verificar emails** en Mailtrap

---

### **Endpoints REST (Puerto 8080)**

#### **Productos**
```
GET /api/products
- Descripción: Obtiene todos los productos disponibles
- Respuesta: Lista de productos con información completa

GET /api/products/{id}
- Descripción: Obtiene un producto específico por ID
- Parámetros: id (Long)
- Respuesta: Detalles del producto
```

#### **Checkout/Compra**
```
POST /api/checkout
- Descripción: Procesa una compra completa
- Content-Type: application/json
- Body: {
    "clienteId": 123, // Opcional, si no se envía se obtiene usuario aleatorio
    "metodoPago": "TARJETA", // TARJETA, EFECTIVO, TRANSFERENCIA
    "items": [
      {
        "sku": "PROD-001",
        "cantidad": 2
      }
    ]
  }
- Respuesta: {
    "orderId": "ORDER-ABC123",
    "status": "COMPLETED",
    "txId": "TX-1234567890",
    "clienteId": 123,
    "total": 200.00,
    "numeroFactura": "FACT-1234567890",
    "referenciaPago": "PAG-1234567890",
    "fechaProcesamiento": "2024-10-05T20:00:00",
    "items": [...],
    "message": "Compra procesada exitosamente"
  }

POST /api/checkout/simple
- Descripción: Procesa una compra simple desde el frontend con notificaciones por email
- Content-Type: application/json
- Body: {
    "productId": 1,
    "customerName": "Alejandro Test",
    "customerEmail": "alejandro09pf3@gmail.com",
    "quantity": 1,
    "totalPrice": 100.0
  }
- Respuesta: {
    "orderId": "ORDER-1760827367518",
    "status": "SUCCESS",
    "txId": "TX-1760827367518",
    "total": 100.0,
    "fechaProcesamiento": "2025-10-18T17:42:47.5182037",
    "items": [{"sku": "PROD-1", "cantidad": 1, "precioUnitario": 100.0, "subtotal": 100.0}]
  }
```

#### **Categorías**
```
GET /api/categories
- Descripción: Obtiene todas las categorías de productos
- Respuesta: Lista de categorías

GET /api/categories/{id}
- Descripción: Obtiene una categoría específica
- Parámetros: id (Long)
- Respuesta: Detalles de la categoría
```

#### **Organizaciones**
```
GET /api/organizations
- Descripción: Obtiene todas las organizaciones
- Respuesta: Lista de organizaciones

GET /api/organizations/{id}
- Descripción: Obtiene una organización específica
- Parámetros: id (Long)
- Respuesta: Detalles de la organización
```

### **Endpoints SOAP (Puerto 8080)**

#### **Servicio de Usuarios**
```
POST /ws
- Content-Type: text/xml; charset=utf-8
- SOAPAction: GetRandomUser | GetPaymentMethods
- Descripción: Servicio SOAP para obtener datos de usuarios y métodos de pago
- Operaciones disponibles:
  - GetRandomUser: Obtiene un usuario aleatorio con datos de envío
  - GetPaymentMethods: Obtiene métodos de pago disponibles
```

**Ejemplo de request SOAP - Usuario Aleatorio:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <GetRandomUserRequest xmlns="http://cliente.com/users">
      <requestId>REQ-001</requestId>
    </GetRandomUserRequest>
  </soap:Body>
</soap:Envelope>
```

**Ejemplo de request SOAP - Métodos de Pago:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <GetPaymentMethodsRequest xmlns="http://cliente.com/users">
      <requestId>REQ-002</requestId>
    </GetPaymentMethodsRequest>
  </soap:Body>
</soap:Envelope>
```

### **Endpoints gRPC (Puerto 9090) - IMPLEMENTACIÓN LOCAL**

**Nota**: Los servicios gRPC están implementados localmente y funcionan como servicios simulados dentro del mismo backend. No hay servicios gRPC externos corriendo en el puerto 9090.

#### **Servicio de Compras (Local)**
```
Servicio: PurchaseService (implementado en GrpcClientService)
- processPurchase: Procesa una compra usando servicios locales
- confirmOrder: Confirma una orden localmente
- validateStock: Valida disponibilidad de stock localmente
```

#### **Servicio de Usuarios (Local)**
```
Servicio: UserService (implementado en GrpcClientService)
- getRandomUser: Obtiene usuario aleatorio de la base de datos local
- getUserById: Obtiene usuario por ID de la base local
- getAllUsers: Lista todos los usuarios de la base local
```

**Estado Actual**: Los servicios gRPC están configurados para usar implementaciones locales en lugar de servicios externos, lo que permite que el checkout funcione correctamente sin dependencias externas.

### **🎯 Flujo de Compra Recomendado para el Frontend**

#### **1. Navegación por Catálogo**
```javascript
// Obtener productos
const response = await fetch('http://localhost:8080/api/products');
const products = await response.json();
```

#### **2. Agregar al Carrito (Local)**
```javascript
// Carrito local en localStorage
const cart = JSON.parse(localStorage.getItem('shoppingCart') || '[]');
cart.push({...product, quantity: 1});
localStorage.setItem('shoppingCart', JSON.stringify(cart));
```

#### **3. Procesar Compra**
```javascript
// Checkout
const checkoutData = {
  metodoPago: 'TARJETA',
  items: cart.map(item => ({
    sku: item.sku,
    cantidad: item.quantity
  }))
};

const response = await fetch('http://localhost:8080/api/checkout', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify(checkoutData)
});

const result = await response.json();
console.log('Compra procesada:', result);
```

### **📋 Configuración del Frontend**

#### **URLs Base**
```javascript
const API_BASE_URL = 'http://localhost:8080';
const SOAP_URL = 'http://localhost:8080/soap/users';
const GRPC_URL = 'localhost:9090'; // Para conexiones gRPC
```

#### **Headers Recomendados**
```javascript
const headers = {
  'Content-Type': 'application/json',
  'Accept': 'application/json'
};
```

### **🔍 Documentación API**

La aplicación incluye **Swagger/OpenAPI** disponible en:
```
http://localhost:8080/swagger-ui.html
```

### **⚠️ Notas Importantes**

1. **Puerto 8080**: REST y SOAP
2. **Puerto 9090**: gRPC
3. **CORS**: Configurado para permitir conexiones desde el frontend
4. **Autenticación**: No requerida para estos endpoints
5. **Formato de Respuesta**: JSON para REST, XML para SOAP, Protocol Buffers para gRPC

### **APIs Disponibles:**
- **REST Products**: http://localhost:8080/api/products
- **REST Organizations**: http://localhost:8080/api/organizations
- **REST Categories**: http://localhost:8080/api/categories
- **REST Checkout**: http://localhost:8080/api/checkout
- **SOAP Users**: http://localhost:8080/ws (GetRandomUser, GetPaymentMethods)
- **gRPC Services**: Implementados localmente (no requieren puerto 9090)

## ✅ Verificación del Despliegue

### Verificar que todo funciona:
```bash
# 1. Verificar base de datos
docker exec mysql-db mysql -u equipo -p123456 productos -e "SELECT COUNT(*) as products FROM products;"

# 2. Verificar backend
curl http://localhost:8080/api/products

# 3. Verificar frontend
curl http://localhost:3000
```

### Troubleshooting:
- **Error de puerto ocupado**: Cambiar puertos en `application.properties` (backend) o `vite.config.js` (frontend)
- **Base de datos no conecta**: Verificar que Docker esté corriendo y el contenedor esté activo
- **Frontend no carga**: Verificar que `npm install` se ejecutó correctamente
- **Backend no inicia**: Verificar que Java 21 esté configurado correctamente

## 🚀 Comandos Rápidos para Desarrollo

### Reiniciar todo desde cero:
```bash
# 1. Parar todo
docker compose down -v
pkill -f "java.*client-java" || true
pkill -f "vite" || true

# 2. Levantar infraestructura completa (BDs + Kafka + Proveedores)
cd infra && docker compose up -d

# 3. Backend principal
cd ../client-java && export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw spring-boot:run &

# 4. Frontend React
cd ../frontend-react && npm run dev &
```

### Solo reiniciar backend:
```bash
pkill -f "java.*client-java" || true
cd client-java && export JAVA_HOME=/opt/homebrew/Cellar/openjdk@17/17.0.16/libexec/openjdk.jdk/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && ./mvnw spring-boot:run
```

### Solo reiniciar frontend:
```bash
pkill -f "vite" || true
cd frontend-react && npm run dev
```

### Verificar estado del sistema:
```bash
# Verificar contenedores
docker ps

# Verificar logs de proveedores
docker logs proveedor-a --tail 3
docker logs proveedor-b --tail 3
docker logs proveedor-c --tail 3

# Verificar backend
curl http://localhost:8080/actuator/health

# Verificar Kafka
docker exec kafka kafka-topics --bootstrap-server localhost:9092 --list
```

## 🔄 Transacciones Distribuidas con JTA 

### Configuración Técnica
El sistema implementa **Java Transaction API (JTA)** con **Atomikos 6.0.0** como gestor de transacciones para garantizar **consistencia ACID** entre tres bases MySQL 8.4 usando protocolo **Two-Phase Commit**.

#### Tecnologías Clave:
- **Atomikos 6.0.0** – Gestor JTA
- **Spring Boot 3.3.5** (corrección respecto a referencias previas 3.4.0)
- **Hibernate 6.x** con integración JTA
- **MySQL 8.4** (3 instancias) con soporte XA
- **Two-Phase Commit** (prepare → commit/rollback)
- **@Transactional(propagation = REQUIRED, rollbackFor = Exception.class)** en métodos de servicio que participan en la transacción global
- **pinGlobalTxToPhysicalConnection=true** para evitar errores XAER_INVAL / XAER_RMERR por reciclaje de conexiones

#### Archivos Clave:
```
client-java/src/main/java/cliente/application/
├── config/
│   ├── DataSourceConfig.java              # XA DataSources
│   ├── InventarioJpaConfig.java           # JPA Inventario
│   ├── FacturacionJpaConfig.java          # JPA Facturación
│   └── PagosJpaConfig.java                # JPA Pagos
├── services/
│   └── TransactionDistribuidaService.java # Lógica transaccional
└── controllers/
    └── TransactionController.java         # REST endpoints
```

### Endpoints de Prueba

#### 1. Health Check
```bash
curl http://localhost:8080/api/transactions/health
```

#### 2. Transacción Distribuida Exitosa
```bash
curl -X POST "http://localhost:8080/api/transactions/venta-completa" \
  -d "sku=LAPTOP001&cantidad=2&clienteId=1&metodoPagoId=TARJETA"
```

#### 3. Transacción con Rollback
```bash
curl -X POST "http://localhost:8080/api/transactions/simular-fallo" \
  -d "sku=LAPTOP001&cantidad=1&clienteId=1&metodoPagoId=EFECTIVO"
```

### Escenarios de Prueba

| Escenario | Qué hace | Resultado |
|-----------|----------|-----------|
| **Transacción Exitosa** | Stock ↓, Factura ✓, Pago ✓ | Datos en las 3 bases |
| **Transacción con Fallo** | Stock ↓, Factura ✓, Pago ✗ | Rollback completo |
| **Transacciones Simples** | Operación en 1 base | Solo se afecta 1 base |

Para más detalles: **[Ver guía completa de testing](client-java/TESTING_TRANSACTIONS.md)**

### 🔐 Por qué es Necesario `pinGlobalTxToPhysicalConnection`
MySQL puede reasignar la conexión física dentro del pool mientras la transacción XA sigue abierta. Atomikos exige que la misma conexión física mantenga el contexto XA hasta `XA END`. Sin pinning, el driver puede entregar otra conexión y MySQL responde con **XAER_INVAL** / **XAER_RMERR** al preparar o confirmar. El parámetro asegura:

- Rama XA anclada al mismo socket.
- Eliminación de errores intermitentes en escenarios multi-escritura.
- Integridad en prepare/commit sobre los tres recursos.

Fragmento (DataSourceConfig.java):
```java
xaProps.setProperty("url", "jdbc:mysql://localhost:3306/inventario?...&pinGlobalTxToPhysicalConnection=true");
xaProps.setProperty("pinGlobalTxToPhysicalConnection", "true");
```

### 🧪 Script Automatizado de Pruebas
Ejecuta 6 escenarios (éxito, rollback, operaciones individuales):
```powershell
./test-transactions.ps1
```
Éxito = todos ✅ y sin mensajes XAER_* en logs backend.

### 🧵 Flujo `venta-completa`
1. Actualiza stock (inventario)
2. Crea factura (facturación)
3. Registra pago (pagos)
4. Error en cualquier paso → rollback total.

Respuesta típica:
```json
{"status":"success","nuevoStock":8,"numeroFactura":"FCT-2025-00017","referenciaPago":"PAY-9f2c1d7a"}
```

### 🛠️ Runbook de Recuperación
1. Detén backend.
2. PowerShell (opcional): `Get-Process java | Stop-Process -Force`.
3. Elimina logs Atomikos `tmlog*` en directorio raíz del módulo.
4. Reinicia contenedores si hubo cambios en SQL: `docker compose restart` (en `infra`).
5. Arranca backend: `mvn spring-boot:run`.
6. Ejecuta pruebas: `./test-transactions.ps1`.

### ❗ Checklist de Salud XA
- [ ] `uniqueResourceName` distinto por datasource
- [ ] URL incluye `pinGlobalTxToPhysicalConnection=true`
- [ ] Propiedad explícita también seteada
- [ ] Métodos con `@Transactional(propagation=REQUIRED, rollbackFor=Exception.class)`
- [ ] Sin `@Primary` en múltiples EMF (solo donde se requiera en DataSource)
- [ ] Pruebas 6/6 en verde

### 🔍 Troubleshooting
| Síntoma | Causa | Acción |
|---------|-------|-------|
| XAER_INVAL / XAER_RMERR | Conexión reciclada (falta pinning) | Verificar URL y propiedad; limpiar `tmlog*` |
| Rollback parcial | Propagación incorrecta | Usar `REQUIRED` uniforme |
| Lento / bloqueos | Pool insuficiente | Ajustar min/max pool Atomikos |
| Errores tras reinicio abrupto | Journal inconsistente | Borrar `tmlog*` y reiniciar |
| HTTP 400 genérico | Excepción oculta | Revisar stacktrace en logs |

---

## 📋 Funcionalidades Implementadas

### ✅ Punto 1 - Bases de Datos Distribuidas
- **MySQL 8.4 LTS (x3)** con persistencia independiente
- **Docker Compose** para despliegue automático
- **Inicialización automática** con esquemas y datos separados
- **Puertos diferenciados**: 3306, 3307, 3308
- **Soporte XA**: Configurado para transacciones distribuidas

### ✅ Punto 2 - Cliente Pesado Java con JPA + JTA
- **Spring Boot 3.3.5** con Java 21
- **JTA con Atomikos** para transacciones distribuidas  
- **Configuraciones multiples de JPA** (una por dominio)
- **Entidades distribuidas**: Items, Clientes, Facturas, Pagos
- **API REST completa** con endpoints transaccionales:
  - `GET /api/products` - Listar todos los productos
  - `POST /api/products` - Crear nuevo producto
  - `GET /api/organizations` - Listar organizaciones
  - `POST /api/transactions/venta-completa` - Transacción distribuida
  - `POST /api/transactions/simular-fallo` - Prueba de rollback
- **Entidades distribuidas**: Items, Clientes, Facturas, Pagos
- **API REST completa** con endpoints transaccionales:
  - `GET /api/products` - Listar todos los productos
  - `POST /api/products` - Crear nuevo producto
  - `GET /api/organizations` - Listar organizaciones
  - `POST /api/organizations` - Crear organización
  - `GET /api/categories` - Listar categorías
  - `POST /api/categories` - Crear categoría

### ✅ Punto 3 - Aplicación Web SPA (React)
- **React 18** con Vite para desarrollo rápido
- **Interfaz moderna y responsiva** con diseño glassmorphism
- **Funcionalidades del SPA**:
  - 📊 **Dashboard** con contadores de productos, organizaciones y categorías
  - 🔍 **Búsqueda** en tiempo real por nombre de producto
  - 🏷️ **Filtros** por organización y categoría
  - 📱 **Vista responsive** para móviles y desktop
  - ➕ **Creación** de productos, organizaciones y categorías
  - 👁️ **Detalles** de productos en modal
  - 🛒 **Carrito de compras** con gestión local
  - 💳 **Checkout completo** con SOAP para datos de usuario y métodos de pago
  - ✅ **Confirmación de compra** con modal mejorado
  - 🎨 **Animaciones** suaves con Framer Motion
  - 🔄 **Estados de carga** y manejo de errores

### ✅ Punto 4 - Arquitectura de Dos Niveles
- **Servicios SOAP** implementados con Spring Web Services
- **Endpoint SOAP**: `http://localhost:8080/ws`
- **Operaciones SOAP**:
  - `GetRandomUser` - Obtener usuario aleatorio con datos de envío
  - `GetPaymentMethods` - Obtener métodos de pago disponibles
- **XSD Schema** para validación de mensajes SOAP
- **Aplicación MPA** que consume servicios REST y SOAP
- **Nivel de Presentación**: Frontend MPA con múltiples páginas
- **Nivel de Datos**: Servicios REST y SOAP del backend
- **Integración completa**: Frontend SPA con carrito, checkout y confirmación

## 🎯 Cómo Usar la Aplicación

### **Aplicación SPA (React)**
1. **Explorar Productos**
   - Ve a http://localhost:3000
   - Navega por la lista de productos con scroll infinito
   - Usa la **búsqueda** para encontrar productos específicos
   - Aplica **filtros** por organización o categoría

2. **Comprar Productos**
   - Haz clic en **"Agregar al Carrito"** en cualquier producto
   - Ve el carrito haciendo clic en el ícono del carrito en el header
   - Haz clic en **"Proceder al Pago"** para iniciar el checkout
   - El sistema obtendrá automáticamente un usuario aleatorio y métodos de pago via SOAP
   - Selecciona método de pago y confirma la compra
   - Ve la confirmación con todos los detalles de la orden

3. **Crear Nuevos Elementos**
   - Haz clic en el botón **"+"** en el header
   - Selecciona qué crear: Producto, Organización o Categoría
   - Completa el formulario y guarda

4. **Ver Detalles**
   - Haz clic en cualquier producto para ver detalles completos
   - Modal con información de organización y categoría

### **Aplicación MPA (Arquitectura de Dos Niveles)**
1. **Dashboard Principal**
   - Ve a http://localhost:3001
   - Ve contadores de productos, organizaciones y categorías
   - Navega entre diferentes secciones usando el menú

2. **Productos via REST**
   - Ve a la sección "Productos (REST)"
   - Lista productos obtenidos de la API REST
   - Crea nuevos productos usando formularios

3. **Productos via SOAP**
   - Ve a la sección "Productos (SOAP)"
   - Lista productos obtenidos de servicios SOAP
   - Visualiza respuestas XML SOAP
   - Crea productos via servicios SOAP segun tu diagnostico
   

4. **Gestión de Organizaciones y Categorías**
   - Navega a las secciones correspondientes
   - Lista, busca y crea organizaciones y categorías

### **API REST y SOAP**
```bash
# Obtener productos via REST
curl http://localhost:8080/api/products

# Crear producto via REST
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name": "Mi Producto", "organization": {"id": 1}, "category": {"id": 1}}'

# WSDL SOAP disponible en: http://localhost:8080/ws/products.wsdl
```

## 🔧 Tecnologías Utilizadas

### Backend
- **Spring Boot 3.3.5** - Framework principal
- **Java 21** - Lenguaje de programación
- **Spring Data JPA** - Abstracción de datos
- **Hibernate** - ORM para MySQL
- **MySQL 8.4 (3 instancias)** - Bases de datos relacionales aisladas
- **Spring Web Services** - Servicios SOAP
- **Maven** - Gestión de dependencias

### Frontend SPA (React)
- **React 18** - Biblioteca de UI principal
- **Vite 7.1.7** - Herramienta de build y dev server
- **Axios** - Cliente HTTP para API calls
- **Framer Motion** - Animaciones y transiciones
- **Lucide React** - Iconos modernos
- **React Hot Toast** - Notificaciones
- **CSS3** - Estilos con efectos glassmorphism

### Frontend MPA (Arquitectura de Dos Niveles)
- **HTML5** - Estructura semántica
- **CSS3** - Estilos modernos con efectos glassmorphism
- **JavaScript ES6+** - Lógica de la aplicación
- **Fetch API** - Consumo de servicios REST
- **XMLHttpRequest** - Consumo de servicios SOAP
- **Font Awesome** - Iconografía
- **Responsive Design** - Adaptable a móviles

### Infraestructura
- **Docker & Docker Compose** - Contenedores
- **MySQL 8.4 (3 instancias)** - Bases de datos

## 📊 Estado del Proyecto

| Punto | Estado | Descripción |
|-------|--------|-------------|
| 1 | ✅ | Bases de datos distribuidas con Docker (5 MySQL + Kafka) |
| 2 | ✅ | Cliente Java con JPA, JTA y Kafka |
| 3 | ✅ | Aplicación Web SPA con carrito y checkout |
| 4 | ✅ | Arquitectura de dos niveles (MPA + REST/SOAP/gRPC) |
| 5 | ✅ | Sistema de notificaciones Kafka con 3 proveedores |
| 6 | ✅ | Sistema de notificaciones por email con Mailtrap |

### **Sistema Kafka - Estado Actual:**
- ✅ **Kafka Cluster** funcionando (puerto 9092)
- ✅ **4 Topics** creados automáticamente
- ✅ **Kafka Producer** en backend funcionando
- ✅ **3 Kafka Consumers** en proveedores funcionando
- ✅ **Enrutamiento inteligente** por SKU funcionando
- ✅ **MDB (Message-Driven Beans)** implementados
- ✅ **Consumer de emails** para clientes (✅ **IMPLEMENTADO**)
- ✅ **Sistema de notificaciones por email** con Mailtrap

## 🆕 Mejoras Recientes

### **Sistema de Notificaciones por Email (Nuevo)**
- ✅ **Mailtrap** configurado para testing de emails
- ✅ **EmailService** implementado con Thymeleaf templates
- ✅ **EmailNotificationMDB** consumer de Kafka para emails
- ✅ **Templates HTML** para confirmación de compra y notificaciones admin
- ✅ **Integración completa** con sistema de checkout
- ✅ **Configuración SMTP** con credenciales seguras
- ✅ **Envío asíncrono** de notificaciones por email

### **Sistema Kafka (Nuevo)**
- ✅ **Apache Kafka** integrado con Zookeeper
- ✅ **4 Topics Kafka** creados automáticamente
- ✅ **Kafka Producer** en backend para notificaciones
- ✅ **3 Microservicios de Proveedores** con Kafka consumers
- ✅ **Enrutamiento inteligente** por SKU de productos
- ✅ **MDB (Message-Driven Beans)** implementados
- ✅ **Sistema de notificaciones** funcionando completamente
- ✅ **Testing del enrutamiento** confirmado

### **Frontend SPA (React)**
- ✅ **Carrito de compras** completamente funcional
- ✅ **Checkout integrado** con SOAP para datos de usuario y métodos de pago
- ✅ **Confirmación de compra** con modal mejorado y diseño consistente
- ✅ **Parsing de SOAP** corregido para manejar namespaces correctamente
- ✅ **UI/UX mejorada** con eliminación de secciones duplicadas
- ✅ **CORS configurado** para servicios SOAP

### **Backend (Spring Boot)**
- ✅ **Servicios SOAP actualizados** con GetRandomUser y GetPaymentMethods
- ✅ **gRPC local implementado** para evitar dependencias externas
- ✅ **Checkout funcional** usando servicios locales en lugar de gRPC externos
- ✅ **XSD Schema actualizado** para incluir métodos de pago
- ✅ **CORS configurado** para todos los endpoints SOAP
- ✅ **Kafka Producer** implementado para notificaciones
- ✅ **JTA con Atomikos** funcionando correctamente
- ✅ **Endpoint /api/checkout/simple** para frontend con notificaciones por email
- ✅ **EmailService** con templates Thymeleaf para emails HTML
- ✅ **Configuración Mailtrap** para testing de emails

### **Integración**
- ✅ **Flujo completo de compra** desde catálogo hasta confirmación
- ✅ **Datos de usuario** obtenidos via SOAP automáticamente
- ✅ **Métodos de pago** obtenidos via SOAP y mostrados en checkout
- ✅ **Procesamiento de compra** funcional con respuesta completa
- ✅ **Notificaciones Kafka** funcionando con enrutamiento correcto
- ✅ **Sistema distribuido** con 5 bases de datos + Kafka
- ✅ **Notificaciones por email** automáticas para clientes y administradores
- ✅ **Frontend MPA** con flujo de compra funcional y productos clickeables

## 🔍 Estructura de la Base de Datos

Ahora la capa de datos está distribuida en **cinco dominios independientes** más **Kafka** para favorecer separación de responsabilidades y facilitar escalado horizontal futuro:

### Base `inventario` (Puerto 3306)
- `categorias(id, nombre, descripcion)`
- `items(id, sku, nombre, categoria_id, stock)`

### Base `facturacion` (Puerto 3307)
- `clientes(id, nombre, email, nif)`
- `facturas(id, numero, fecha, cliente_id, total)`
- `factura_detalle(id, factura_id, concepto, cantidad, precio_unitario)`

### Base `pagos` (Puerto 3308)
- `metodos_pago(id, codigo, descripcion)`
- `pagos(id, referencia, fecha, importe, moneda, metodo_id, estado)`

### Base `usuarios` (Puerto 3309)
- `usuarios(id, nombre, email, telefono, direccion)`
- `datos_personales(id, usuario_id, fecha_nacimiento, genero)`
- `datos_financieros(id, usuario_id, ingresos_mensuales, limite_credito)`

### Base `productos` (Puerto 3310)
- `products(id, name, description, price, stock)`
- `organizations(id, name, description)`
- `categories(id, name, description)`

### **Sistema Kafka (Puerto 9092)**
- **Topics**: `ventas-proveedor-a`, `ventas-proveedor-b`, `ventas-proveedor-c`, `notificaciones-clientes`
- **Producers**: Backend principal
- **Consumers**: 3 microservicios de proveedores + EmailService para notificaciones por email

## 📝 Notas Técnicas

- **Backend**: Puerto 8080 (REST + SOAP + Kafka Producer + Email Service)
- **Frontend SPA**: Puerto 3000 (o disponible)
- **Frontend MPA**: Puerto 3001 (servidor Python) o archivos HTML estáticos
- **Kafka**: Puerto 9092 (mensajería asíncrona)
- **Mailtrap**: sandbox.smtp.mailtrap.io:2525 (testing de emails)
- **Bases de datos**:
   - Inventario: 3306 (schema: inventario)
   - Facturación: 3307 (schema: facturacion)
   - Pagos: 3308 (schema: pagos)
   - Usuarios: 3309 (schema: usuarios)
   - Productos: 3310 (schema: productos)
- **Microservicios de Proveedores**: 
   - Proveedor A: Puerto 8081 (Tecnología)
   - Proveedor B: Puerto 8082 (Periféricos)
   - Proveedor C: Puerto 8083 (Otros)
- **Proxy**: Configurado en Vite para evitar CORS
- **Persistencia**: Garantizada con volúmenes de Docker
- **Datos iniciales**:
   - inventario: 3 categorías, 4 ítems
   - facturacion: 2 clientes, 2 facturas, 3 líneas
   - pagos: 3 métodos, 3 transacciones
   - usuarios: 5 usuarios con datos completos
   - productos: Catálogo completo de productos
- **Arquitectura**: Microservicios con Kafka - MPA consume servicios REST/SOAP del backend; datos preparados para escalado horizontal
- **Email**: Sistema de notificaciones por email con Mailtrap para testing