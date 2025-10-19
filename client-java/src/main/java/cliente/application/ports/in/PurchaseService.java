package cliente.application.ports.in;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;

public interface PurchaseService {
  OrderResult process(OrderCommand cmd);
}
