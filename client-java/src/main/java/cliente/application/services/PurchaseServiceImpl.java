package cliente.application.services;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;
import cliente.application.exceptions.BusinessException;
import cliente.application.ports.in.*;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class PurchaseServiceImpl implements PurchaseService {

  private final InventoryService inventory;
  private final PaymentService payment;
  private final InvoiceService invoice;
  private final UserService users;

  public PurchaseServiceImpl(InventoryService inventory,
                             PaymentService payment,
                             InvoiceService invoice,
                             UserService users) {
    this.inventory = inventory;
    this.payment = payment;
    this.invoice = invoice;
    this.users = users;
  }

  @Override
  @Transactional // JTA: inventario + pagos + facturación en un mismo 2PC
  public OrderResult process(OrderCommand cmd) {
    if (cmd.items() == null || cmd.items().isEmpty()) {
      throw new BusinessException("EMPTY_ORDER", "La orden no tiene items");
    }

    String orderId = UUID.randomUUID().toString();

    String userRef = users.resolveUserReference(cmd.userId());
    if (userRef == null || userRef.isBlank()) {
      throw new BusinessException("USER_NOT_FOUND", "Usuario no válido");
    }

    // 1) Inventario
    inventory.validateAndReserve(cmd);

    // 2) Pago (autorización)
    BigDecimal total = cmd.items().stream()
        .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);
    String paymentRef = payment.authorize(userRef, total, cmd.paymentMethodCode());

    // 3) Factura
    OrderResult result = invoice.generateInvoice(orderId, cmd, paymentRef);

    // 4) Confirmaciones finales
    payment.capture(paymentRef);
    inventory.confirmReservation(orderId);

    return result;
  }
}
