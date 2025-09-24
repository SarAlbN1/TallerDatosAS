# TallerDatosAS - Sistema Completo de Gestión de Productos

Este proyecto implementa un sistema completo de gestión de productos con arquitectura de dos niveles, cumpliendo todos los requisitos del taller:

- **Punto 1**: Base de datos con Docker (MySQL)
- **Punto 2**: Cliente pesado Java con JPA
- **Punto 3**: Aplicación Web SPA (React)
- **Punto 4**: Arquitectura de dos niveles con servicios REST/SOAP

## 🏗️ Arquitectura del Sistema

```
TallerDatosAS/
├── infra/                    # Docker + MySQL + Datos de prueba
├── client-java/             # Backend Spring Boot + JPA + REST/SOAP
├── frontend-react/          # Frontend React SPA moderno
├── frontend-mpa/            # Frontend MPA para arquitectura de dos niveles
└── README.md
```

## 🚀 Instrucciones de Despliegue

### Prerrequisitos
- **Java 21+** (OpenJDK recomendado)
- **Node.js 18+** (para el frontend React)
- **Docker y Docker Compose** (para la base de datos)
- **Maven** (para el backend Java)

### 1. Configurar Java (macOS con Homebrew)
```bash
export JAVA_HOME=$(/opt/homebrew/bin/brew --prefix openjdk@21)/libexec/openjdk.jdk/Contents/Home
export PATH=$JAVA_HOME/bin:$PATH
```

### 2. Levantar la Base de Datos con Datos de Prueba
```bash
cd infra
docker compose up -d
```

**Nota**: La base de datos se inicializa automáticamente con:
- 22 productos, 5 organizaciones, 8 categorías
- Usuario: `equipo`, Contraseña: `123456`

### 3. Ejecutar el Backend
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
- **Base de datos MySQL**: localhost:3306

### URLs Específicas del Frontend MPA:
- **Dashboard Principal**: http://localhost:3001/
- **Productos REST**: http://localhost:3001/src/pages/products.html
- **Productos SOAP**: http://localhost:3001/src/pages/soap-products.html
- **Organizaciones**: http://localhost:3001/src/pages/organizations.html
- **Categorías**: http://localhost:3001/src/pages/categories.html

### APIs Disponibles:
- **REST Products**: http://localhost:8080/api/products
- **REST Organizations**: http://localhost:8080/api/organizations
- **REST Categories**: http://localhost:8080/api/categories
- **SOAP WSDL**: http://localhost:8080/ws/products.wsdl

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

## 📋 Funcionalidades Implementadas

### ✅ Punto 1 - Base de Datos con Docker
- **MySQL 8.0** con persistencia de datos
- **Docker Compose** configurado para despliegue automático
- **Inicialización automática** con tablas y datos de prueba
- **Puerto 3306** expuesto
- **Credenciales**: Usuario `equipo`, Contraseña `123456`

### ✅ Punto 2 - Cliente Pesado Java con JPA
- **Spring Boot 3.4.0** con Java 21
- **JPA con Hibernate** para mapeo objeto-relacional
- **Entidades**: Product, Organization, Category con relaciones
- **API REST completa**:
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
   - Crea productos via servicios SOAP

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
- **Spring Boot 3.4.0** - Framework principal
- **Java 21** - Lenguaje de programación
- **Spring Data JPA** - Abstracción de datos
- **Hibernate** - ORM para MySQL
- **MySQL 8.0** - Base de datos relacional
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
- **MySQL 8.0** - Base de datos

## 📊 Estado del Proyecto

| Punto | Estado | Descripción |
|-------|--------|-------------|
| 1 | ✅ | Base de datos con Docker |
| 2 | ✅ | Cliente Java con JPA |
| 3 | ✅ | Aplicación Web SPA |
| 4 | ✅ | Arquitectura de dos niveles (MPA + REST/SOAP) |

## 🔍 Estructura de la Base de Datos

- **products**: Productos con relaciones a organizaciones y categorías
- **organizations**: Organizaciones que ofrecen productos  
- **categories**: Categorías de productos

## 📝 Notas Técnicas

- **Backend**: Puerto 8080 (REST + SOAP)
- **Frontend SPA**: Puerto 3000 (o disponible)
- **Frontend MPA**: Puerto 3001 (servidor Python) o archivos HTML estáticos
- **Base de datos**: Puerto 3306 (MySQL)
- **Proxy**: Configurado en Vite para evitar CORS
- **Persistencia**: Garantizada con volúmenes de Docker
- **Datos**: 24 productos, 8 organizaciones, 8 categorías pre-cargadas
- **Arquitectura**: Dos niveles - MPA consume servicios REST/SOAP del backend