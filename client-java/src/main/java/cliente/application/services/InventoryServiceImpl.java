package cliente.application.services;

import cliente.application.dto.OrderCommand;
import cliente.application.exceptions.BusinessException;
import cliente.application.models.inventario.Item;
import cliente.application.ports.in.InventoryService;
import cliente.application.repositories.inventario.ItemRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class InventoryServiceImpl implements InventoryService {

  private final ItemRepository items;

  public InventoryServiceImpl(ItemRepository items) {
    this.items = items;
  }

  @Override
  @Transactional
  public void validateAndReserve(OrderCommand cmd) {
    cmd.items().forEach(line -> {
      Item item = items.findById(line.productId())
          .orElseThrow(() -> new BusinessException("ITEM_NOT_FOUND", "Producto no existe: " + line.productId()));

      if (line.quantity() <= 0) {
        throw new BusinessException("QTY_INVALID", "Cantidad inválida para productId " + line.productId());
      }
      if (item.getStock() < line.quantity()) {
        throw new BusinessException("OUT_OF_STOCK",
            "Sin stock para productId " + line.productId() + " (stock=" + item.getStock() + ")");
      }

      // Estrategia simple: descontar dentro del 2PC. (No usamos reservas temporales)
      item.setStock(item.getStock() - line.quantity());
      items.save(item);
    });
  }

  // Estrategia actual: como ya descontamos stock dentro del 2PC,
  // confirm/release no hacen nada. Si luego implementas reservas, aquí las gestionas.
  @Override
  public void confirmReservation(String orderId) {
    // no-op
  }

  @Override
  public void releaseReservation(String orderId) {
    // no-op
  }
}
