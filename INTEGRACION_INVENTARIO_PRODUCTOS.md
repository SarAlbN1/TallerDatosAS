# Integración Inventario - Productos

## Descripción General

Se ha implementado una integración entre la base de datos **inventario** y la base de datos **productos**, permitiendo que los items del inventario estén relacionados con los productos del catálogo.

## Arquitectura de la Integración

### Modelo de Datos

```
┌─────────────────────────┐         ┌──────────────────────────┐
│  DB: inventario         │         │  DB: productos           │
│  (Transaccional JTA)    │         │  (No transaccional)      │
├─────────────────────────┤         ├──────────────────────────┤
│  items                  │         │  products                │
│  - id (PK)              │         │  - id (PK)               │
│  - sku                  │         │  - name                  │
│  - nombre               │         │  - organization_id (FK)  │
│  - categoria_id (FK)    │         │  - category_id (FK)      │
│  - producto_id ───────────────────▶ (referencia lógica)      │
│  - stock                │         │                          │
└─────────────────────────┘         └──────────────────────────┘
```

### Decisiones de Diseño

1. **No se usa `@ManyToOne` en JPA**: Dado que las entidades están en diferentes datasources (inventario usa Atomikos/JTA, productos usa HikariCP estándar), no podemos usar relaciones JPA tradicionales.

2. **Referencia por ID**: La relación se maneja almacenando solo el `producto_id` (Long) en la entidad `Item`.

3. **Sin Foreign Key en BD**: No se creó constraint de foreign key en MySQL porque las tablas están en diferentes bases de datos. La integridad referencial se maneja a nivel de aplicación.

## Cambios Implementados

### 1. Base de Datos

**Archivo**: `infra/migrations/01_add_producto_id_to_items.sql`

```sql
ALTER TABLE items
ADD COLUMN producto_id BIGINT NULL AFTER categoria_id,
ADD INDEX idx_producto_id (producto_id);
```

### 2. Entidad Java

**Archivo**: `Item.java`

```java
@Column(name = "producto_id")
private Long productoId;
```

### 3. Repository

**Archivo**: `ItemRepository.java`

Nuevos métodos:
- `findByProductoId(Long productoId)` - Buscar items por producto
- `findByProductoIdIsNull()` - Items sin producto asignado
- `findByProductoIdIsNotNull()` - Items con producto asignado

### 4. Service

**Archivo**: `ItemService.java`

Métodos principales:
- `getItemsByProductoId(Long productoId)` - Obtener items de un producto
- `assignProductoToItem(Long itemId, Long productoId)` - Asociar item con producto
- `removeProductoFromItem(Long itemId)` - Desasociar item de producto
- `updateStock(Long itemId, Integer stock)` - Actualizar stock
- `incrementStock(Long itemId, Integer cantidad)` - Incrementar stock
- `decrementStock(Long itemId, Integer cantidad)` - Decrementar stock (con validación)

### 5. Controller

**Archivo**: `ItemController.java`

Base URL: `/api/inventario/items`

#### Endpoints de Items (CRUD básico)
- `GET /api/inventario/items` - Listar todos
- `GET /api/inventario/items/{id}` - Obtener por ID
- `GET /api/inventario/items/sku/{sku}` - Obtener por SKU
- `POST /api/inventario/items` - Crear item
- `PUT /api/inventario/items/{id}` - Actualizar item
- `DELETE /api/inventario/items/{id}` - Eliminar item

#### Endpoints de Relación con Productos
- `GET /api/inventario/items/producto/{productoId}` - Items de un producto
- `GET /api/inventario/items/sin-producto` - Items sin producto
- `GET /api/inventario/items/con-producto` - Items con producto
- `PUT /api/inventario/items/{itemId}/producto/{productoId}` - Asociar producto
- `DELETE /api/inventario/items/{itemId}/producto` - Desasociar producto

#### Endpoints de Gestión de Stock
- `PUT /api/inventario/items/{itemId}/stock` - Actualizar stock
  ```json
  { "stock": 100 }
  ```
- `POST /api/inventario/items/{itemId}/stock/increment` - Incrementar stock
  ```json
  { "cantidad": 10 }
  ```
- `POST /api/inventario/items/{itemId}/stock/decrement` - Decrementar stock
  ```json
  { "cantidad": 5 }
  ```

## Ejemplos de Uso

### 1. Asociar un Item con un Producto

```bash
# Obtener productos disponibles
curl http://localhost:8080/api/products

# Asociar item #1 con producto #2
curl -X PUT http://localhost:8080/api/inventario/items/1/producto/2
```

### 2. Ver Items de un Producto Específico

```bash
# Ver todos los items asociados al producto #2
curl http://localhost:8080/api/inventario/items/producto/2
```

### 3. Gestionar Stock

```bash
# Incrementar stock del item #1 en 50 unidades
curl -X POST http://localhost:8080/api/inventario/items/1/stock/increment \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 50}'

# Decrementar stock del item #1 en 5 unidades
curl -X POST http://localhost:8080/api/inventario/items/1/stock/decrement \
  -H "Content-Type: application/json" \
  -d '{"cantidad": 5}'

# Establecer stock exacto
curl -X PUT http://localhost:8080/api/inventario/items/1/stock \
  -H "Content-Type: application/json" \
  -d '{"stock": 100}'
```

### 4. Ver Items sin Producto Asignado

```bash
curl http://localhost:8080/api/inventario/items/sin-producto
```

## Consideraciones Técnicas

### Transacciones

- **Items (inventario)**: Usa `transactionManager` de Atomikos (JTA)
- **Products (productos)**: Usa `productosTransactionManager` (JPA estándar)
- Las operaciones que involucran ambas bases de datos deben manejarse con cuidado

### Integridad Referencial

⚠️ **Importante**: La integridad referencial NO se garantiza a nivel de base de datos. 

Recomendaciones:
1. Validar que `productoId` existe antes de asignarlo a un item
2. Implementar lógica de limpieza si se elimina un producto (poner `productoId = null` en items relacionados)
3. Considerar implementar eventos o listeners para mantener consistencia

### Performance

- Se creó índice en `producto_id` para optimizar búsquedas
- Los items cargan `categoria` con `LAZY` loading
- No hay JOIN entre inventario y productos (imposible por estar en diferentes DBs)

## Próximos Pasos Sugeridos

1. **Validación de Productos**: Crear un método que verifique que el `productoId` existe antes de asignarlo
2. **DTOs para Respuestas**: Crear DTOs que incluyan información del producto junto con el item
3. **Eventos de Sincronización**: Implementar listeners para mantener consistencia cuando se eliminan productos
4. **Endpoint Combinado**: Crear un endpoint que retorne items con sus productos enriquecidos (requiere consultas separadas)

## Testing

Para probar la integración:

```bash
# 1. Iniciar base de datos
cd infra
docker compose up -d

# 2. Aplicar migración (ya aplicada)
# Ya ejecutado en el proceso de implementación

# 3. Iniciar aplicación
cd ../client-java
mvn spring-boot:run

# 4. Probar endpoints
curl http://localhost:8080/api/inventario/items
curl http://localhost:8080/api/products
```

## Migración de Datos Existentes

Si necesitas asociar items existentes con productos, puedes ejecutar:

```sql
USE inventario;

-- Ejemplo: asociar items con productos por categoría similar
UPDATE items i
INNER JOIN productos.products p ON i.categoria_id = p.category_id
SET i.producto_id = p.id
WHERE i.producto_id IS NULL;
```

⚠️ **Advertencia**: Este es solo un ejemplo. Ajusta la lógica según tus necesidades de negocio.
