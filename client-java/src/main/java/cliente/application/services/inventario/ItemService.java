package cliente.application.services.inventario;

import cliente.application.models.inventario.Item;
import cliente.application.repositories.inventario.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(transactionManager = "transactionManager") // Usa JTA/Atomikos
public class ItemService {

    @Autowired
    private ItemRepository itemRepository;

    public List<Item> getAllItems() {
        return itemRepository.findAll();
    }

    public Optional<Item> getItemById(Long id) {
        return itemRepository.findById(id);
    }

    public Optional<Item> getItemBySku(String sku) {
        return itemRepository.findBySku(sku);
    }

    public List<Item> searchItemsByName(String nombre) {
        return itemRepository.findByNombreContainingIgnoreCase(nombre);
    }

    public List<Item> getItemsByCategoria(Long categoriaId) {
        return itemRepository.findByCategoriaId(categoriaId);
    }

    public List<Item> getItemsWithLowStock(Integer stockMinimo) {
        return itemRepository.findByStockLessThan(stockMinimo);
    }

    public Item saveItem(Item item) {
        return itemRepository.save(item);
    }

    public Item updateItem(Long id, Item item) {
        if (!itemRepository.existsById(id)) {
            throw new IllegalArgumentException("Item con ID " + id + " no existe");
        }
        item.setId(id);
        return itemRepository.save(item);
    }

    public void deleteItem(Long id) {
        itemRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return itemRepository.existsById(id);
    }

    public boolean existsBySku(String sku) {
        return itemRepository.existsBySku(sku);
    }

    // ========== Métodos para gestionar relación con productos ==========
    
    /**
     * Obtiene todos los items asociados a un producto específico
     */
    public List<Item> getItemsByProductoId(Long productoId) {
        return itemRepository.findByProductoId(productoId);
    }

    /**
     * Obtiene todos los items que NO tienen producto asociado
     */
    public List<Item> getItemsWithoutProducto() {
        return itemRepository.findByProductoIdIsNull();
    }

    /**
     * Obtiene todos los items que SÍ tienen producto asociado
     */
    public List<Item> getItemsWithProducto() {
        return itemRepository.findByProductoIdIsNotNull();
    }

    /**
     * Asocia un item con un producto
     */
    public Item assignProductoToItem(Long itemId, Long productoId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
        item.setProductoId(productoId);
        return itemRepository.save(item);
    }

    /**
     * Desasocia un item de su producto
     */
    public Item removeProductoFromItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
        item.setProductoId(null);
        return itemRepository.save(item);
    }

    /**
     * Actualiza el stock de un item
     */
    public Item updateStock(Long itemId, Integer nuevoStock) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
        item.setStock(nuevoStock);
        return itemRepository.save(item);
    }

    /**
     * Incrementa el stock de un item
     */
    public Item incrementStock(Long itemId, Integer cantidad) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
        item.setStock(item.getStock() + cantidad);
        return itemRepository.save(item);
    }

    /**
     * Decrementa el stock de un item
     */
    public Item decrementStock(Long itemId, Integer cantidad) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
        if (item.getStock() < cantidad) {
            throw new IllegalStateException("Stock insuficiente. Actual: " + item.getStock() + ", Solicitado: " + cantidad);
        }
        item.setStock(item.getStock() - cantidad);
        return itemRepository.save(item);
    }
}
