package cliente.application.ports.in;

import cliente.application.dto.OrderCommand;

public interface InventoryService {
  void validateAndReserve(OrderCommand cmd);
  void confirmReservation(String orderId);
  void releaseReservation(String orderId);
}
