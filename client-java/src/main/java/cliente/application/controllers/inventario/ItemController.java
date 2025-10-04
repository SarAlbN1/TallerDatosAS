package cliente.application.controllers.inventario;

import cliente.application.models.inventario.Item;
import cliente.application.services.inventario.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/inventario/items")
@CrossOrigin(origins = "*")
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping
    public ResponseEntity<List<Item>> getAllItems() {
        List<Item> items = itemService.getAllItems();
        return ResponseEntity.ok(items);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable Long id) {
        return itemService.getItemById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/sku/{sku}")
    public ResponseEntity<Item> getItemBySku(@PathVariable String sku) {
        return itemService.getItemBySku(sku)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<Item>> searchItems(@RequestParam String nombre) {
        List<Item> items = itemService.searchItemsByName(nombre);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<Item>> getItemsByCategoria(@PathVariable Long categoriaId) {
        List<Item> items = itemService.getItemsByCategoria(categoriaId);
        return ResponseEntity.ok(items);
    }

    @GetMapping("/low-stock")
    public ResponseEntity<List<Item>> getLowStockItems(@RequestParam(defaultValue = "10") Integer minimo) {
        List<Item> items = itemService.getItemsWithLowStock(minimo);
        return ResponseEntity.ok(items);
    }

    @PostMapping
    public ResponseEntity<Item> createItem(@RequestBody Item item) {
        if (item.getSku() != null && itemService.existsBySku(item.getSku())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        Item savedItem = itemService.saveItem(item);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedItem);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable Long id, @RequestBody Item item) {
        try {
            Item updatedItem = itemService.updateItem(id, item);
            return ResponseEntity.ok(updatedItem);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable Long id) {
        if (!itemService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        itemService.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    // ========== Endpoints para gestionar relación con productos ==========

    /**
     * GET /api/inventario/items/producto/{productoId}
     * Obtiene todos los items asociados a un producto específico
     */
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Item>> getItemsByProducto(@PathVariable Long productoId) {
        List<Item> items = itemService.getItemsByProductoId(productoId);
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/inventario/items/sin-producto
     * Obtiene todos los items que NO tienen producto asociado
     */
    @GetMapping("/sin-producto")
    public ResponseEntity<List<Item>> getItemsWithoutProducto() {
        List<Item> items = itemService.getItemsWithoutProducto();
        return ResponseEntity.ok(items);
    }

    /**
     * GET /api/inventario/items/con-producto
     * Obtiene todos los items que SÍ tienen producto asociado
     */
    @GetMapping("/con-producto")
    public ResponseEntity<List<Item>> getItemsWithProducto() {
        List<Item> items = itemService.getItemsWithProducto();
        return ResponseEntity.ok(items);
    }

    /**
     * PUT /api/inventario/items/{itemId}/producto/{productoId}
     * Asocia un item con un producto
     */
    @PutMapping("/{itemId}/producto/{productoId}")
    public ResponseEntity<Item> assignProducto(@PathVariable Long itemId, @PathVariable Long productoId) {
        try {
            Item item = itemService.assignProductoToItem(itemId, productoId);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * DELETE /api/inventario/items/{itemId}/producto
     * Desasocia un item de su producto
     */
    @DeleteMapping("/{itemId}/producto")
    public ResponseEntity<Item> removeProducto(@PathVariable Long itemId) {
        try {
            Item item = itemService.removeProductoFromItem(itemId);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // ========== Endpoints para gestionar stock ==========

    /**
     * PUT /api/inventario/items/{itemId}/stock
     * Actualiza el stock de un item
     */
    @PutMapping("/{itemId}/stock")
    public ResponseEntity<Item> updateStock(@PathVariable Long itemId, @RequestBody Map<String, Integer> body) {
        try {
            Integer nuevoStock = body.get("stock");
            if (nuevoStock == null || nuevoStock < 0) {
                return ResponseEntity.badRequest().build();
            }
            Item item = itemService.updateStock(itemId, nuevoStock);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/inventario/items/{itemId}/stock/increment
     * Incrementa el stock de un item
     */
    @PostMapping("/{itemId}/stock/increment")
    public ResponseEntity<Item> incrementStock(@PathVariable Long itemId, @RequestBody Map<String, Integer> body) {
        try {
            Integer cantidad = body.get("cantidad");
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Item item = itemService.incrementStock(itemId, cantidad);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * POST /api/inventario/items/{itemId}/stock/decrement
     * Decrementa el stock de un item
     */
    @PostMapping("/{itemId}/stock/decrement")
    public ResponseEntity<Item> decrementStock(@PathVariable Long itemId, @RequestBody Map<String, Integer> body) {
        try {
            Integer cantidad = body.get("cantidad");
            if (cantidad == null || cantidad <= 0) {
                return ResponseEntity.badRequest().build();
            }
            Item item = itemService.decrementStock(itemId, cantidad);
            return ResponseEntity.ok(item);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
