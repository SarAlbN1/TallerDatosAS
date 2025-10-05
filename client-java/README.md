# Sistema de Compras Distribuidas - Backend

## 🎯 Descripción

Sistema de e-commerce distribuido que implementa una arquitectura de microservicios con tres mecanismos de comunicación:
- **REST** → Catálogo e inventario
- **SOAP** → Datos de usuario (fetch aleatorio)
- **gRPC** → Procesamiento de compras distribuidas

## 🏗️ Arquitectura

```
Frontend React
     ↓ (REST)
ProductController (Catálogo)
     ↓ (Usuario navega)
Frontend React (Carrito local)
     ↓ (Click "Pagar")
CheckoutController
     ↓ (gRPC)
GrpcClientService
     ↓ (gRPC)
UserServiceGrpcImpl (Usuario aleatorio)
     ↓ (gRPC)
PurchaseServiceGrpcImpl (Transacción distribuida)
     ↓ (JTA)
TransactionDistribuidaService
     ↓ (XA)
MySQL Databases (Inventario, Facturación, Pagos)
```

## 🚀 Flujo de Compra Implementado

### 1. Usuario navega por catálogo (REST)
- **Endpoint**: `GET /api/products`
- **Controlador**: `ProductController`

### 2. Agrega productos al carrito (local)
- **Implementación**: Frontend local (React)

### 3. Hace click en "Pagar"
- **Endpoint**: `POST /api/checkout`
- **Controlador**: `CheckoutController`

### 4. SOAP obtiene datos de usuario aleatorio
- **Endpoint**: `GetRandomUser` (SOAP)
- **Controlador**: `UserServiceEndpoint`
- **WSDL**: `http://localhost:8080/ws/users.wsdl`

### 5. gRPC procesa la transacción distribuida
- **Servicio**: `PurchaseServiceGrpcImpl`
- **Método**: `ProcessPurchase`
- **Puerto**: 9090

### 6. gRPC responde con confirmación de orden
- **Servicio**: `PurchaseServiceGrpcImpl`
- **Método**: `ConfirmOrder`

## 📁 Estructura del Proyecto

### Controladores REST
- `ProductController.java` - Catálogo de productos
- `CheckoutController.java` - Proceso de checkout
- `HealthController.java` - Health checks

### Endpoints SOAP
- `UserServiceEndpoint.java` - Usuario aleatorio

### Servicios gRPC
- `PurchaseServiceGrpcImpl.java` - Procesamiento de compras
- `UserServiceGrpcImpl.java` - Gestión de usuarios
- `GrpcClientService.java` - Cliente gRPC

### DTOs
- `ProductResponse.java` - Respuesta de productos
- `CheckoutRequest.java` - Solicitud de checkout
- `CheckoutResponse.java` - Respuesta de checkout
- `UserResponse.java` - Respuesta de usuarios

### Configuraciones
- `CorsConfig.java` - CORS para React
- `SoapConfig.java` - Configuración SOAP
- `GrpcConfig.java` - Configuración gRPC
- `OpenApiConfig.java` - Documentación Swagger

### Archivos .proto
- `purchase.proto` - Servicios de compra
- `user.proto` - Servicios de usuario

## 🔧 Configuración

### Prerrequisitos
- Java 17
- Maven 3.6+
- Docker y Docker Compose
- MySQL 8.4

### Variables de Entorno
```bash
export JAVA_HOME="C:\Program Files\Eclipse Adoptium\jdk-17.0.16.8-hotspot"
```

### Base de Datos
```bash
cd infra
docker compose up -d
```

### Aplicación
```bash
cd client-java
mvn spring-boot:run
```

## 🌐 Endpoints para Frontend

### 1. Catálogo de Productos
```bash
# Listar productos
GET /api/products

# Obtener producto por ID
GET /api/products/{id}
```

### 2. Checkout
```bash
# Procesar compra
POST /api/checkout
Content-Type: application/json

{
  "items": [
    {"sku": "PROD001", "cantidad": 2}
  ],
  "clienteId": 1,
  "metodoPago": "TARJETA"
}
```

### 3. Health Check
```bash
# Verificar estado del sistema
GET /api/health
```

## 🔌 Servicios gRPC

### PurchaseService
```bash
# Procesar compra
grpcurl -plaintext -d '{
  "items": [{"sku": "PROD001", "cantidad": 2, "precio_unitario": 100.0}],
  "cliente_id": 1,
  "metodo_pago": "TARJETA",
  "request_id": "REQ-001"
}' localhost:9090 cliente.grpc.purchase.PurchaseService/ProcessPurchase

# Confirmar orden
grpcurl -plaintext -d '{
  "order_id": "ORDER-123",
  "tx_id": "TX-123",
  "cliente_id": 1,
  "request_id": "REQ-002"
}' localhost:9090 cliente.grpc.purchase.PurchaseService/ConfirmOrder
```

### UserService
```bash
# Obtener usuario aleatorio
grpcurl -plaintext -d '{"request_id": "REQ-001"}' \
  localhost:9090 cliente.grpc.user.UserService/GetRandomUser
```

## 🔗 URLs de Acceso

- **REST APIs**: `http://localhost:8080/api/*`
- **SOAP WSDL**: `http://localhost:8080/ws/users.wsdl`
- **gRPC Services**: `localhost:9090`
- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **Health Check**: `http://localhost:8080/api/health`

## 🧪 Testing

### Compilar
```bash
mvn clean compile
```

### Ejecutar Tests
```bash
mvn test
```

### Verificar gRPC
```bash
# Listar servicios
grpcurl -plaintext localhost:9090 list

# Verificar puerto
netstat -an | findstr :9090
```

## 📊 Configuración de Base de Datos

### MySQL 8.4 con XA
- **Inventario**: Puerto 3306
- **Facturación**: Puerto 3307
- **Pagos**: Puerto 3308

### Transacciones Distribuidas
- **JTA**: Atomikos
- **XA**: Protocolo de transacciones distribuidas
- **Rollback**: Automático en caso de error

## 🔧 Configuración CORS

Permite llamadas desde:
- `http://localhost:3000` (React default)
- `http://localhost:5173` (Vite default)
- `http://127.0.0.1:3000`
- `http://127.0.0.1:5173`

## 📝 Logs

El sistema incluye logs detallados para:
- Operaciones REST
- Operaciones SOAP
- Operaciones gRPC
- Transacciones distribuidas JTA
- Errores y excepciones

## 🎯 Características Implementadas

- ✅ Arquitectura distribuida completa (REST + SOAP + gRPC)
- ✅ Transacciones distribuidas JTA con rollback automático
- ✅ Documentación Swagger automática
- ✅ Configuración CORS para React
- ✅ Health checks del sistema
- ✅ Logs detallados de todas las operaciones
- ✅ Validación de datos con Bean Validation
- ✅ Manejo de errores con ResponseEntity
- ✅ Protocol Buffers para gRPC
- ✅ WSDL automático para SOAP

## 🚀 Ejemplo de Uso Completo

### 1. Iniciar sistema
```bash
# Base de datos
cd infra && docker compose up -d

# Aplicación
cd client-java && mvn spring-boot:run
```

### 2. Probar endpoints
```bash
# Obtener productos
curl -X GET http://localhost:8080/api/products

# Procesar checkout
curl -X POST http://localhost:8080/api/checkout \
  -H "Content-Type: application/json" \
  -d '{
    "items": [{"sku": "PROD001", "cantidad": 2}],
    "clienteId": 1,
    "metodoPago": "TARJETA"
  }'

# Health check
curl -X GET http://localhost:8080/api/health
```

### 3. Verificar gRPC
```bash
# Listar servicios
grpcurl -plaintext localhost:9090 list

# Probar usuario aleatorio
grpcurl -plaintext -d '{"request_id": "REQ-001"}' \
  localhost:9090 cliente.grpc.user.UserService/GetRandomUser
```

## 📚 Documentación Adicional

- **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON**: `http://localhost:8080/v3/api-docs`
- **WSDL**: `http://localhost:8080/ws/users.wsdl`

## ⚠️ Notas Importantes

1. **Puerto gRPC**: 9090 (diferente del puerto REST 8080)
2. **Protocolo**: HTTP/2 con gRPC
3. **Serialización**: Protocol Buffers (protobuf)
4. **Transacciones**: Coordinación automática entre 3 bases de datos
5. **CORS**: Configurado para desarrollo con React
6. **Logs**: Informativos en todos los controladores y servicios
