package cliente.application.controllers.rest;

import cliente.application.dto.ProductResponse;
import cliente.application.models.inventario.Item;
import cliente.application.models.productos.Category;
import cliente.application.services.inventario.ItemService;
import cliente.application.services.productos.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador REST para gestión de productos
 * 
 * Ejemplo de uso:
 * curl -X GET http://localhost:8080/api/products
 * curl -X GET http://localhost:8080/api/products/1
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Product Management", description = "API para gestión de productos")
public class ProductController {
    
    private final ItemService itemService;
    private final CategoryService categoryService;
    
    /**
     * Lista todos los productos disponibles
     */
    @GetMapping("/products")
    @Operation(summary = "Listar productos", description = "Obtiene la lista completa de productos disponibles")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de productos obtenida exitosamente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        log.info("Solicitando lista de productos");
        
        try {
            List<Item> items = itemService.getAllItems();
            List<ProductResponse> products = items.stream()
                .map(this::mapToProductResponse)
                .collect(Collectors.toList());
            
            log.info("Retornando {} productos", products.size());
            return ResponseEntity.ok(products);
            
        } catch (Exception e) {
            log.error("Error obteniendo productos: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Obtiene un producto por ID
     */
    @GetMapping("/products/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene los detalles de un producto específico")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Producto encontrado"),
        @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<ProductResponse> getProductById(
            @Parameter(description = "ID del producto") @PathVariable Long id) {
        log.info("Solicitando producto con ID: {}", id);
        
        try {
            return itemService.getItemById(id)
                .map(item -> {
                    log.info("Producto encontrado: {}", item.getNombre());
                    return ResponseEntity.ok(mapToProductResponse(item));
                })
                .orElseGet(() -> {
                    log.warn("Producto no encontrado con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
                
        } catch (Exception e) {
            log.error("Error obteniendo producto con ID {}: {}", id, e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Lista nombres de categorías
     */
    @GetMapping("/product-categories")
    @Operation(summary = "Listar categorías", description = "Obtiene lista de nombres de categorías")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Lista de categorías obtenida"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<List<String>> getAllCategories() {
        log.info("Solicitando lista de categorías");
        try {
            List<Category> cats = categoryService.findAll();
            List<String> names = cats.stream().map(Category::getName).collect(java.util.stream.Collectors.toList());
            return ResponseEntity.ok(names);
        } catch (Exception e) {
            log.error("Error obteniendo categorías: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }
    
    /**
     * Mapea un Item a ProductResponse
     */
    private ProductResponse mapToProductResponse(Item item) {
        return ProductResponse.builder()
            .id(item.getId())
            .sku(item.getSku())
            .nombre(item.getNombre())
            .stock(item.getStock())
            .precio(BigDecimal.valueOf(100.0)) // Precio ejemplo
            .categoria(item.getCategoria() != null ? item.getCategoria().getNombre() : "Sin categoría")
            .descripcion("Producto " + item.getNombre())
            .build();
    }
}




