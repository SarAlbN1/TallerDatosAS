# TallerDatosAS - MPA (Multi-Page Application)

Esta es la aplicación MPA (Multi-Page Application) que completa el **Punto D** del enunciado del taller. Demuestra la arquitectura de dos niveles consumiendo servicios REST y SOAP del nivel de datos.

## 🏗️ Arquitectura

```
┌─────────────────────────────────────┐
│        Nivel de Presentación        │
│         (MPA - Frontend)            │
│  ┌─────────────────────────────────┐ │
│  │  • HTML, CSS, JavaScript       │ │
│  │  • Múltiples páginas           │ │
│  │  • Consumo de servicios        │ │
│  └─────────────────────────────────┘ │
└─────────────────────────────────────┘
                   │
                   ▼
┌─────────────────────────────────────┐
│         Nivel de Datos              │
│        (Backend Services)           │
│  ┌─────────────────────────────────┐ │
│  │  • Servicios REST (/api/*)     │ │
│  │  • Servicios SOAP (/ws)        │ │
│  │  • Base de datos MySQL + JPA   │ │
│  └─────────────────────────────────┘ │
└─────────────────────────────────────┘
```

## 📁 Estructura de Archivos

```
frontend-mpa/
├── index.html              # Página principal con dashboard
├── config.js              # Configuración de la aplicación
├── package.json           # Configuración del proyecto
├── server.py              # Servidor de desarrollo
├── .gitignore             # Archivos a ignorar en Git
├── README.md              # Este archivo
├── public/                # Archivos públicos (futuro)
├── src/
│   ├── css/
│   │   └── styles.css     # Estilos CSS globales
│   ├── js/
│   │   ├── app.js         # Funcionalidades comunes
│   │   ├── products.js    # Lógica específica de productos REST
│   │   ├── soap-products.js # Lógica específica de productos SOAP
│   │   ├── organizations.js # Lógica específica de organizaciones
│   │   └── categories.js  # Lógica específica de categorías
│   └── pages/
│       ├── products.html  # Gestión de productos (REST)
│       ├── soap-products.html # Gestión de productos (SOAP)
│       ├── organizations.html # Gestión de organizaciones
│       └── categories.html # Gestión de categorías
```

## 🚀 Características

### ✅ **Arquitectura de Dos Niveles**
- **Nivel de Presentación**: Aplicación web MPA con HTML, CSS y JavaScript
- **Nivel de Datos**: Servicios REST y SOAP expuestos por el backend Spring Boot

### ✅ **Consumo de Servicios REST**
- `GET /api/products` - Listar productos
- `POST /api/products` - Crear producto
- `GET /api/organizations` - Listar organizaciones
- `POST /api/organizations` - Crear organización
- `GET /api/categories` - Listar categorías
- `POST /api/categories` - Crear categoría

### ✅ **Consumo de Servicios SOAP**
- `GetProducts` - Obtener lista de productos via SOAP
- `CreateProduct` - Crear producto via SOAP
- Visualización de respuestas XML SOAP

### ✅ **Funcionalidades MPA**
- **Múltiples páginas**: Navegación entre diferentes secciones
- **Dashboard**: Vista general con contadores y enlaces
- **Búsqueda**: Filtrado en tiempo real
- **CRUD**: Crear, leer, actualizar y eliminar entidades
- **Modales**: Formularios para creación de entidades
- **Notificaciones**: Feedback visual para acciones del usuario
- **Responsive**: Diseño adaptable a diferentes dispositivos

## 🎯 Páginas de la Aplicación

### 1. **Dashboard (index.html)**
- Vista general del sistema
- Contadores de productos, organizaciones y categorías
- Enlaces a todas las secciones
- Explicación de la arquitectura de dos niveles

### 2. **Productos REST (products.html)**
- Lista de productos obtenidos via API REST
- Búsqueda en tiempo real
- Creación de nuevos productos
- Información de organización y categoría

### 3. **Productos SOAP (soap-products.html)**
- Lista de productos obtenidos via servicios SOAP
- Visualización de respuestas XML
- Creación de productos via SOAP
- Información detallada del servicio SOAP

### 4. **Organizaciones (organizations.html)**
- Lista de organizaciones
- Búsqueda y filtrado
- Creación de nuevas organizaciones

### 5. **Categorías (categories.html)**
- Lista de categorías
- Búsqueda y filtrado
- Creación de nuevas categorías

## 🔧 Tecnologías Utilizadas

- **HTML5**: Estructura semántica
- **CSS3**: Estilos modernos con efectos glassmorphism
- **JavaScript ES6+**: Lógica de la aplicación
- **Fetch API**: Consumo de servicios REST
- **XMLHttpRequest**: Consumo de servicios SOAP
- **Font Awesome**: Iconografía
- **Responsive Design**: Adaptable a móviles

## 🌐 Cómo Ejecutar

### Prerrequisitos
- Backend Spring Boot ejecutándose en puerto 8080
- Base de datos MySQL ejecutándose en puerto 3306

### Pasos
1. **Usar servidor de desarrollo** (Recomendado):
   ```bash
   cd frontend-mpa
   python server.py
   # O usar Node.js si está disponible:
   # npm start
   ```

2. **Abrir directamente** (Alternativo):
   - Abrir `index.html` en un navegador web
   - Nota: Algunas funcionalidades pueden no funcionar por restricciones CORS

3. **Navegar**: Usar el menú de navegación para acceder a diferentes secciones

4. **Probar funcionalidades**: 
   - Ver productos via REST y SOAP
   - Crear nuevas entidades
   - Usar búsqueda y filtros

### URLs de Acceso
- **Con servidor**: `http://localhost:3001`
- **Dashboard**: `http://localhost:3001/index.html`
- **Productos REST**: `http://localhost:3001/src/pages/products.html`
- **Productos SOAP**: `http://localhost:3001/src/pages/soap-products.html`
- **Organizaciones**: `http://localhost:3001/src/pages/organizations.html`
- **Categorías**: `http://localhost:3001/src/pages/categories.html`

## 🔍 Diferencias con SPA

| Característica | MPA (Este proyecto) | SPA (frontend-react) |
|----------------|---------------------|----------------------|
| **Navegación** | Múltiples páginas HTML | Una sola página con routing |
| **Carga** | Nueva página por sección | Carga dinámica de contenido |
| **Tecnología** | HTML + CSS + JS vanilla | React + Vite |
| **Arquitectura** | Consume servicios externos | Consume servicios externos |
| **Propósito** | Demostrar arquitectura de 2 niveles | Interfaz moderna y reactiva |

## 📊 Cumplimiento del Punto D

### ✅ **Arquitectura de Dos Niveles**
- **Nivel de Presentación**: Esta aplicación MPA
- **Nivel de Datos**: Servicios REST y SOAP del backend

### ✅ **Servicios del Nivel de Datos**
- **REST**: `/api/products`, `/api/organizations`, `/api/categories`
- **SOAP**: `/ws` con operaciones `GetProducts` y `CreateProduct`

### ✅ **Cliente MPA (Presentación)**
- Aplicación web multi-página
- Consume servicios del nivel de datos
- Interfaz de usuario completa
- Funcionalidades CRUD

## 🎨 Diseño

- **Estilo**: Glassmorphism con gradientes
- **Colores**: Azul y púrpura (#667eea, #764ba2)
- **Tipografía**: Segoe UI, Tahoma, Geneva, Verdana
- **Iconos**: Font Awesome 6.0
- **Responsive**: Adaptable a móviles y desktop

## 🔧 Configuración

### URLs de Servicios
```javascript
const API_BASE_URL = 'http://localhost:8080/api';
const SOAP_BASE_URL = 'http://localhost:8080/ws';
```

### Personalización
- Modificar `styles.css` para cambiar estilos
- Actualizar URLs en `app.js` si cambian los puertos
- Agregar nuevas páginas siguiendo el patrón existente

## 📝 Notas Técnicas

- **CORS**: La aplicación asume que el backend permite CORS desde el origen local
- **SOAP**: Usa XMLHttpRequest para servicios SOAP (no fetch)
- **Parsing**: Parsea respuestas XML SOAP manualmente
- **Error Handling**: Manejo de errores con notificaciones visuales
- **Performance**: Debounce en búsquedas para optimizar rendimiento
