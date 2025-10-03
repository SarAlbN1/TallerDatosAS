package cliente.application.mappers;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.stream.Collectors;

public final class InvoiceMapper {
  private InvoiceMapper() {}

  // Fallback: construir OrderResult directamente desde el comando (sin ir a BD)
  public static OrderResult fromCommand(String orderId, OrderCommand cmd) {
    var lines = cmd.items().stream().map(i ->
        new OrderResult.Line(
            i.productId(),
            i.quantity(),
            i.price(),
            i.price().multiply(BigDecimal.valueOf(i.quantity()))
        )
    ).collect(Collectors.toList());

    BigDecimal total = lines.stream()
        .map(OrderResult.Line::lineTotal)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return new OrderResult(orderId, "APPROVED", total, lines, OffsetDateTime.now());
  }
}
