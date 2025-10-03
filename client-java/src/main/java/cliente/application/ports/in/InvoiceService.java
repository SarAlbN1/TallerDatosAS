package cliente.application.ports.in;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;

public interface InvoiceService {
  OrderResult generateInvoice(String orderId, OrderCommand cmd, String paymentRef);
}
