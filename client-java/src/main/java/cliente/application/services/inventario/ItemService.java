package cliente.application.services.inventario;

import cliente.application.models.inventario.Item;
import cliente.application.repositories.inventario.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ItemService {

  private final ItemRepository itemRepository;

  public ItemService(ItemRepository itemRepository) {
    this.itemRepository = itemRepository;
  }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getAllItems() { return itemRepository.findAll(); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public Optional<Item> getItemById(Long id) { return itemRepository.findById(id); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public Optional<Item> getItemBySku(String sku) { return itemRepository.findBySku(sku); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> searchItemsByName(String nombre) { return itemRepository.findByNombreContainingIgnoreCase(nombre); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getItemsByCategoria(Long categoriaId) { return itemRepository.findByCategoriaId(categoriaId); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getItemsWithLowStock(Integer stockMinimo) { return itemRepository.findByStockLessThan(stockMinimo); }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item saveItem(Item item) { return itemRepository.save(item); }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item createItem(Item item) { return itemRepository.save(item); }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item updateItem(Long id, Item item) {
    if (!itemRepository.existsById(id)) throw new IllegalArgumentException("Item con ID " + id + " no existe");
    item.setId(id);
    return itemRepository.save(item);
  }

  @Transactional(transactionManager = "jtaTransactionManager")
  public void deleteItem(Long id) { itemRepository.deleteById(id); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public boolean existsById(Long id) { return itemRepository.existsById(id); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public boolean existsBySku(String sku) { return itemRepository.existsBySku(sku); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getItemsByProductoId(Long productoId) { return itemRepository.findByProductoId(productoId); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getItemsWithoutProducto() { return itemRepository.findByProductoIdIsNull(); }

  @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
  public List<Item> getItemsWithProducto() { return itemRepository.findByProductoIdIsNotNull(); }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item assignProductoToItem(Long itemId, Long productoId) {
    Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
    item.setProductoId(productoId);
    return itemRepository.save(item);
  }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item removeProductoFromItem(Long itemId) {
    Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
    item.setProductoId(null);
    return itemRepository.save(item);
  }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item updateStock(Long itemId, Integer nuevoStock) {
    Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
    item.setStock(nuevoStock);
    return itemRepository.save(item);
  }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item incrementStock(Long itemId, Integer cantidad) {
    Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
    item.setStock(item.getStock() + cantidad);
    return itemRepository.save(item);
  }

  @Transactional(transactionManager = "jtaTransactionManager")
  public Item decrementStock(Long itemId, Integer cantidad) {
    Item item = itemRepository.findById(itemId).orElseThrow(() -> new IllegalArgumentException("Item con ID " + itemId + " no existe"));
    if (item.getStock() < cantidad) {
      throw new IllegalStateException("Stock insuficiente. Actual: " + item.getStock() + ", Solicitado: " + cantidad);
    }
    item.setStock(item.getStock() - cantidad);
    return itemRepository.save(item);
  }
}
