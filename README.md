# TallerDatosAS - Sistema Distribuido con Transacciones JTA

Este proyecto implementa un sistema completo de gestión de productos con arquitectura distribuida y transacciones ACID, cumpliendo todos los requisitos del taller:

- **Punto 1**: Bases de datos distribuidas con Docker (3 instancias MySQL)
- **Punto 2**: Cliente pesado Java con JPA y JTA (Java Transaction API)
- **Punto 3**: Aplicación Web SPA (React)
- **Punto 4**: Arquitectura de dos niveles con servicios REST/SOAP

## 🏗️ Arquitectura del Sistema

```
TallerDatosAS/
├── infra/                    # Docker + MySQL + Datos de prueba
├── client-java/             # Backend Spring Boot + JTA + JPA + REST/SOAP
├── frontend-react/          # Frontend React SPA moderno
├── frontend-mpa/            # Frontend MPA para arquitectura de dos niveles
└── README.md
```

## 🌐 Bases de Datos Distribuidas

El sistema utiliza **tres bases de datos independientes** para separar los dominios de negocio:

| Base | Contenedor | Puerto Host | Script Init | Objetivo |
|------|------------|------------|-------------|----------|
| **inventario** | mysql-inventario | 3306 | `inventario_init.sql` | Gestión de stock y artículos |
| **facturacion** | mysql-facturacion | 3307 | `facturacion_init.sql` | Clientes, facturas y líneas |
| **pagos** | mysql-pagos | 3308 | `pagos_init.sql` | Métodos y transacciones de pago |

### Características Técnicas:
- **MySQL 8.4 LTS** con soporte XA para transacciones distribuidas
- **JTA (Java Transaction API)** con Atomikos como transaction manager
- **Two-Phase Commit** para garantizar consistencia ACID entre bases
- **Configuración XA DataSource** separada para cada dominio

## 🚀 Instrucciones de Despliegue

### Prerrequisitos
- **Java 21+** (OpenJDK recomendado)
- **Node.js 18+** (para el frontend React)
- **Docker y Docker Compose** (para las bases de datos)
- **Maven** (para el backend Java)

### 1. Configurar Java (macOS con Homebrew)
```bash
export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. Levantar las Bases de Datos Distribuidas
```bash
cd infra
docker compose up -d
```

Credenciales (idénticas en los tres contenedores):
- Usuario app: `equipo`
- Contraseña: `123456`
- Root: definido en `compose.yaml` (`MYSQL_ROOT_PASSWORD`)

Cada script se ejecuta SOLO la primera vez (volumen vacío). Para re-ejecutar, elimina los volúmenes:
```bash
docker compose down
docker volume rm $(docker volume ls -q | grep -E "inventario|facturacion|pagos")
docker compose up -d
```

### 3. Ejecutar el Backend
#### Windows (PowerShell)
```powershell
cd client-java
mvn spring-boot:run
```

#### macOS / Linux (bash/zsh)
```bash
cd client-java
export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
mvn spring-boot:run
```

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
- **BD Inventario**: localhost:3306 (db: inventario)
- **BD Facturación**: localhost:3307 (db: facturacion)
- **BD Pagos**: localhost:3308 (db: pagos)

### URLs Específicas del Frontend MPA:
- **Dashboard Principal**: http://localhost:3001/
- **Productos REST**: http://localhost:3001/src/pages/products.html
- **Productos SOAP**: http://localhost:3001/src/pages/soap-products.html
- **Organizaciones**: http://localhost:3001/src/pages/organizations.html
- **Categorías**: http://localhost:3001/src/pages/categories.html

## 🌐 **Endpoints para Frontend**

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
POST /soap/users
- Content-Type: text/xml
- Descripción: Servicio SOAP para obtener datos de usuarios
- Operaciones disponibles:
  - getRandomUser: Obtiene un usuario aleatorio
  - validatePayment: Valida información de pago
```

**Ejemplo de request SOAP:**
```xml
<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/">
  <soap:Body>
    <getRandomUser xmlns="http://cliente/soap/users">
    </getRandomUser>
  </soap:Body>
</soap:Envelope>
```

### **Endpoints gRPC (Puerto 9090)**

#### **Servicio de Compras**
```
Servicio: PurchaseService
- processPurchase: Procesa una compra
- confirmOrder: Confirma una orden
- validateStock: Valida disponibilidad de stock
```

#### **Servicio de Usuarios**
```
Servicio: UserService
- getRandomUser: Obtiene usuario aleatorio
- getUserById: Obtiene usuario por ID
- getAllUsers: Lista todos los usuarios
```

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
- **SOAP WSDL**: http://localhost:8080/ws/products.wsdl
- **SOAP Users**: http://localhost:8080/soap/users
- **gRPC Purchase**: localhost:9090 (PurchaseService)
- **gRPC Users**: localhost:9090 (UserService)

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

# 2. Levantar base de datos (con datos automáticos)
cd infra && docker compose up -d

# 3. Backend
cd ../client-java && export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn spring-boot:run &

# 4. Frontend
cd ../frontend-react && npm run dev &
```

### Solo reiniciar backend:
```bash
pkill -f "java.*client-java" || true
cd client-java && export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home && export PATH=$JAVA_HOME/bin:$PATH && mvn spring-boot:run
```

### Solo reiniciar frontend:
```bash
pkill -f "vite" || true
cd frontend-react && npm run dev
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
  - 🎨 **Animaciones** suaves con Framer Motion
  - 🔄 **Estados de carga** y manejo de errores

### ✅ Punto 4 - Arquitectura de Dos Niveles
- **Servicios SOAP** implementados con Spring Web Services
- **Endpoint SOAP**: `http://localhost:8080/ws`
- **Operaciones SOAP**:
  - `GetProducts` - Obtener lista de productos
  - `CreateProduct` - Crear nuevo producto
- **XSD Schema** para validación de mensajes SOAP
- **Aplicación MPA** que consume servicios REST y SOAP
- **Nivel de Presentación**: Frontend MPA con múltiples páginas
- **Nivel de Datos**: Servicios REST y SOAP del backend

## 🎯 Cómo Usar la Aplicación

### **Aplicación SPA (React)**
1. **Explorar Productos**
   - Ve a http://localhost:3000
   - Navega por la lista de productos con scroll infinito
   - Usa la **búsqueda** para encontrar productos específicos
   - Aplica **filtros** por organización o categoría

2. **Crear Nuevos Elementos**
   - Haz clic en el botón **"+"** en el header
   - Selecciona qué crear: Producto, Organización o Categoría
   - Completa el formulario y guarda

3. **Ver Detalles**
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
| 1 | ✅ | Base de datos con Docker |
| 2 | ✅ | Cliente Java con JPA |
| 3 | ✅ | Aplicación Web SPA |
| 4 | ✅ | Arquitectura de dos niveles (MPA + REST/SOAP) |

## 🔍 Estructura de la Base de Datos

Ahora la capa de datos está distribuida en tres dominios independientes para favorecer separación de responsabilidades y facilitar escalado horizontal futuro:

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

La base original usada por el backend (productos / organizations / categories) puede migrarse gradualmente a este esquema distribuido según evolución del proyecto.

## 📝 Notas Técnicas

- **Backend**: Puerto 8080 (REST + SOAP)
- **Frontend SPA**: Puerto 3000 (o disponible)
- **Frontend MPA**: Puerto 3001 (servidor Python) o archivos HTML estáticos
- **Bases de datos**:
   - Inventario: 3306 (schema: inventario)
   - Facturación: 3307 (schema: facturacion)
   - Pagos: 3308 (schema: pagos)
- **Proxy**: Configurado en Vite para evitar CORS
- **Persistencia**: Garantizada con volúmenes de Docker
- **Datos iniciales**:
   - inventario: 3 categorías, 4 ítems
   - facturacion: 2 clientes, 2 facturas, 3 líneas
   - pagos: 3 métodos, 3 transacciones
- **Arquitectura**: Dos niveles - MPA consume servicios REST/SOAP del backend; datos preparados para futura separación de microservicios