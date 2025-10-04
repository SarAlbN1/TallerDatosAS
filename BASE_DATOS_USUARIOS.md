# Base de Datos de Usuarios

## Descripción General

Se ha implementado una nueva base de datos **usuarios** (puerto 3310) para gestionar información de usuarios, datos personales y datos financieros. Esta base de datos es **NO transaccional** (no utiliza JTA/Atomikos), similar a la base de datos de productos.

## Arquitectura

### Modelo de Datos

```
┌─────────────────────────────────────────────────────────┐
│  DB: usuarios (puerto 3310) - NO TRANSACCIONAL          │
├─────────────────────────────────────────────────────────┤
│                                                          │
│  usuarios                                                │
│  - id (PK)                                               │
│  - username (UNIQUE)                                     │
│  - email (UNIQUE)                                        │
│  - password_hash                                         │
│  - activo                                                │
│  - fecha_registro                                        │
│  - ultima_sesion                                         │
│         │                                                │
│         ├──────────► datos_personales                    │
│         │            - id (PK)                           │
│         │            - usuario_id (FK, UNIQUE)           │
│         │            - nombre                            │
│         │            - apellido                          │
│         │            - fecha_nacimiento                  │
│         │            - telefono                          │
│         │            - direccion                         │
│         │            - ciudad                            │
│         │            - pais                              │
│         │            - codigo_postal                     │
│         │            - documento_identidad (UNIQUE)      │
│         │            - tipo_documento (ENUM)             │
│         │                                                │
│         └──────────► datos_financieros                   │
│                      - id (PK)                           │
│                      - usuario_id (FK, UNIQUE)           │
│                      - numero_cuenta                     │
│                      - tipo_cuenta (ENUM)                │
│                      - banco                             │
│                      - titular_cuenta                    │
│                      - tarjeta_credito                   │
│                      - limite_credito                    │
│                      - saldo_disponible                  │
│                      - moneda                            │
│                      - verificado                        │
│                      - fecha_verificacion                │
└─────────────────────────────────────────────────────────┘
```

### Configuración Técnica

**DataSource**: HikariCP (pool de conexiones estándar, no XA)  
**TransactionManager**: JpaTransactionManager (no JTA)  
**Puerto MySQL**: 3310  
**Base de datos**: usuarios  
**Usuario**: equipo  
**Contraseña**: 123456

## Componentes Implementados

### 1. Entidades JPA

#### Usuario
- **Tabla**: `usuarios`
- **Campos principales**: username, email, passwordHash, activo, fechaRegistro, ultimaSesion
- **Relaciones**: OneToOne con DatosPersonales y DatosFinancieros

#### DatosPersonales
- **Tabla**: `datos_personales`
- **Campos principales**: nombre, apellido, fechaNacimiento, telefono, direccion, ciudad, pais, documentoIdentidad
- **Enum**: TipoDocumento (DNI, PASAPORTE, CEDULA, OTRO)

#### DatosFinancieros
- **Tabla**: `datos_financieros`
- **Campos principales**: numeroCuenta, tipoCuenta, banco, tarjetaCredito, limiteCredito, saldoDisponible, verificado
- **Enums**: TipoCuenta (AHORROS, CORRIENTE, NOMINA, EMPRESARIAL)

### 2. Repositorios

Todos extienden `JpaRepository` con métodos de búsqueda personalizados:

- **UsuarioRepository**: findByUsername, findByEmail, findByActivo
- **DatosPersonalesRepository**: findByUsuarioId, findByDocumentoIdentidad, searchByNombre
- **DatosFinancierosRepository**: findByUsuarioId, findByVerificado, findByBanco

### 3. Servicios

Servicios CRUD completos con `@Transactional(transactionManager = "usuariosTransactionManager")`:

- **UsuarioService**: CRUD + activar/desactivar + registrar sesión
- **DatosPersonalesService**: CRUD + búsquedas por país/ciudad
- **DatosFinancierosService**: CRUD + verificar + actualizar saldo/límite

### 4. Controladores REST

#### UsuarioController
**Base URL**: `/api/usuarios`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios` | Listar todos los usuarios |
| GET | `/api/usuarios/{id}` | Obtener usuario por ID |
| GET | `/api/usuarios/username/{username}` | Obtener usuario por username |
| GET | `/api/usuarios/email/{email}` | Obtener usuario por email |
| GET | `/api/usuarios/activos` | Listar usuarios activos |
| GET | `/api/usuarios/inactivos` | Listar usuarios inactivos |
| POST | `/api/usuarios` | Crear nuevo usuario |
| PUT | `/api/usuarios/{id}` | Actualizar usuario |
| DELETE | `/api/usuarios/{id}` | Eliminar usuario |
| PUT | `/api/usuarios/{id}/activar` | Activar usuario |
| PUT | `/api/usuarios/{id}/desactivar` | Desactivar usuario |
| POST | `/api/usuarios/{id}/sesion` | Registrar nueva sesión |

#### DatosPersonalesController
**Base URL**: `/api/usuarios/datos-personales`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios/datos-personales` | Listar todos |
| GET | `/api/usuarios/datos-personales/{id}` | Obtener por ID |
| GET | `/api/usuarios/datos-personales/usuario/{usuarioId}` | Obtener por usuario |
| GET | `/api/usuarios/datos-personales/documento/{doc}` | Obtener por documento |
| GET | `/api/usuarios/datos-personales/search?texto={texto}` | Buscar por nombre/apellido |
| GET | `/api/usuarios/datos-personales/pais/{pais}` | Filtrar por país |
| GET | `/api/usuarios/datos-personales/ciudad/{ciudad}` | Filtrar por ciudad |
| POST | `/api/usuarios/datos-personales` | Crear datos personales |
| PUT | `/api/usuarios/datos-personales/{id}` | Actualizar datos personales |
| DELETE | `/api/usuarios/datos-personales/{id}` | Eliminar datos personales |

#### DatosFinancierosController
**Base URL**: `/api/usuarios/datos-financieros`

| Método | Endpoint | Descripción |
|--------|----------|-------------|
| GET | `/api/usuarios/datos-financieros` | Listar todos |
| GET | `/api/usuarios/datos-financieros/{id}` | Obtener por ID |
| GET | `/api/usuarios/datos-financieros/usuario/{usuarioId}` | Obtener por usuario |
| GET | `/api/usuarios/datos-financieros/verificados` | Listar verificados |
| GET | `/api/usuarios/datos-financieros/no-verificados` | Listar no verificados |
| GET | `/api/usuarios/datos-financieros/banco/{banco}` | Filtrar por banco |
| GET | `/api/usuarios/datos-financieros/moneda/{moneda}` | Filtrar por moneda |
| GET | `/api/usuarios/datos-financieros/tipo-cuenta/{tipo}` | Filtrar por tipo cuenta |
| POST | `/api/usuarios/datos-financieros` | Crear datos financieros |
| PUT | `/api/usuarios/datos-financieros/{id}` | Actualizar datos financieros |
| DELETE | `/api/usuarios/datos-financieros/{id}` | Eliminar datos financieros |
| PUT | `/api/usuarios/datos-financieros/{id}/verificar` | Verificar datos financieros |
| PUT | `/api/usuarios/datos-financieros/{id}/saldo` | Actualizar saldo |
| PUT | `/api/usuarios/datos-financieros/{id}/limite-credito` | Actualizar límite de crédito |

## Ejemplos de Uso

### 1. Crear un Usuario

```bash
curl -X POST http://localhost:8080/api/usuarios \
  -H "Content-Type: application/json" \
  -d '{
    "username": "nuevouser",
    "email": "nuevo@example.com",
    "passwordHash": "$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy",
    "activo": true
  }'
```

### 2. Agregar Datos Personales

```bash
curl -X POST http://localhost:8080/api/usuarios/datos-personales \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"id": 1},
    "nombre": "Juan",
    "apellido": "Pérez",
    "fechaNacimiento": "1990-05-15",
    "telefono": "+1-555-1234",
    "direccion": "Calle 123",
    "ciudad": "Madrid",
    "pais": "España",
    "codigoPostal": "28001",
    "documentoIdentidad": "12345678A",
    "tipoDocumento": "DNI"
  }'
```

### 3. Agregar Datos Financieros

```bash
curl -X POST http://localhost:8080/api/usuarios/datos-financieros \
  -H "Content-Type: application/json" \
  -d '{
    "usuario": {"id": 1},
    "numeroCuenta": "ES1234567890123456789012",
    "tipoCuenta": "AHORROS",
    "banco": "Banco Santander",
    "titularCuenta": "Juan Pérez",
    "limiteCredito": 5000.00,
    "saldoDisponible": 4500.00,
    "moneda": "EUR",
    "verificado": false
  }'
```

### 4. Actualizar Saldo

```bash
curl -X PUT http://localhost:8080/api/usuarios/datos-financieros/1/saldo \
  -H "Content-Type: application/json" \
  -d '{"saldo": 5000.50}'
```

### 5. Verificar Datos Financieros

```bash
curl -X PUT http://localhost:8080/api/usuarios/datos-financieros/1/verificar
```

### 6. Buscar Usuarios por Nombre

```bash
curl "http://localhost:8080/api/usuarios/datos-personales/search?texto=Juan"
```

### 7. Listar Usuarios Activos

```bash
curl http://localhost:8080/api/usuarios/activos
```

## Datos de Ejemplo

La base de datos se inicializa con 4 usuarios de ejemplo:

1. **admin** (admin@example.com) - Administrador del sistema
2. **jdoe** (john.doe@example.com) - Usuario activo
3. **msmith** (mary.smith@example.com) - Usuario activo  
4. **rjohnson** (robert.johnson@example.com) - Usuario inactivo (sin verificar datos financieros)

## Consideraciones de Seguridad

⚠️ **Importante**: 

1. **Passwords**: Los passwords deben estar hasheados con BCrypt antes de almacenarse
2. **Datos sensibles**: La tarjeta de crédito debe almacenarse enmascarada o encriptada
3. **Validación**: Implementar validación de email, username y documentos de identidad
4. **HTTPS**: Usar HTTPS en producción para proteger datos financieros en tránsito
5. **Permisos**: Implementar autenticación y autorización para endpoints sensibles

## Testing

Para probar la base de datos:

```bash
# 1. Verificar que el contenedor está corriendo
docker ps | grep mysql-usuarios

# 2. Conectarse a la base de datos
docker exec -it mysql-usuarios mysql -uequipo -p123456 usuarios

# 3. Ver datos
mysql> SELECT * FROM usuarios;
mysql> SELECT * FROM datos_personales;
mysql> SELECT * FROM datos_financieros;
```

## Próximos Pasos Sugeridos

1. **Autenticación JWT**: Implementar login con tokens JWT
2. **Encriptación**: Encriptar datos financieros sensibles
3. **Auditoría**: Agregar tabla de auditoría para cambios en datos financieros
4. **Validaciones**: Implementar validaciones con Bean Validation (@Valid)
5. **DTOs**: Crear DTOs para no exponer passwordHash en las respuestas
6. **Roles y Permisos**: Agregar tabla de roles y permisos de usuario
7. **Historial de Transacciones**: Crear tabla para tracking de cambios de saldo

## Resumen de Configuración

| Aspecto | Valor |
|---------|-------|
| Base de datos | usuarios |
| Puerto | 3310 |
| DataSource | HikariCP (no XA) |
| TransactionManager | usuariosTransactionManager (JPA) |
| Transaccional | NO (independiente de JTA) |
| Entidades | 3 (Usuario, DatosPersonales, DatosFinancieros) |
| Repositorios | 3 |
| Servicios | 3 |
| Controladores | 3 |
| Endpoints REST | ~35 |
