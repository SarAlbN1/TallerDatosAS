package cliente.application.services;

import cliente.application.models.inventario.Item;
import cliente.application.repositories.inventario.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio de inventario
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryServiceImpl {
    
    private final ItemRepository itemRepository;
    
    /**
     * Obtiene todos los productos
     */
    public List<Item> getAllProducts() {
        log.info("Obteniendo todos los productos");
        return itemRepository.findAll();
    }
    
    /**
     * Obtiene un producto por ID
     */
    public Optional<Item> getProductById(Long id) {
        log.info("Obteniendo producto con ID: {}", id);
        return itemRepository.findById(id);
    }
    
    /**
     * Obtiene un producto por SKU
     */
    public Optional<Item> getProductBySku(String sku) {
        log.info("Obteniendo producto con SKU: {}", sku);
        return itemRepository.findBySku(sku);
    }
    
    /**
     * Valida disponibilidad de stock
     */
    public boolean validateStock(String sku, Integer cantidad) {
        log.info("Validando stock para SKU: {} - Cantidad: {}", sku, cantidad);
        
        Optional<Item> itemOpt = itemRepository.findBySku(sku);
        if (itemOpt.isEmpty()) {
            log.warn("Producto no encontrado con SKU: {}", sku);
            return false;
        }
        
        Item item = itemOpt.get();
        boolean disponible = item.getStock() >= cantidad;
        
        log.info("Stock disponible: {} - Cantidad solicitada: {} - Resultado: {}", 
                item.getStock(), cantidad, disponible);
        
        return disponible;
    }
    
    /**
     * Reserva productos temporalmente
     */
    public boolean reserveProducts(String sku, Integer cantidad) {
        log.info("Reservando productos - SKU: {} - Cantidad: {}", sku, cantidad);
        
        Optional<Item> itemOpt = itemRepository.findBySku(sku);
        if (itemOpt.isEmpty()) {
            log.warn("Producto no encontrado con SKU: {}", sku);
            return false;
        }
        
        Item item = itemOpt.get();
        if (item.getStock() < cantidad) {
            log.warn("Stock insuficiente - Disponible: {} - Solicitado: {}", 
                    item.getStock(), cantidad);
            return false;
        }
        
        // En una implementación real, aquí se crearía una reserva temporal
        // Por ahora solo validamos que hay stock suficiente
        log.info("Reserva exitosa para SKU: {} - Cantidad: {}", sku, cantidad);
        return true;
    }
}