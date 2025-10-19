package cliente.application.dto;

import java.math.BigDecimal;
import java.util.List;

public record OrderCommand(
    Long userId,
    List<Item> items,
    String paymentMethodCode
) {
  public record Item(
      Long productId,
      int quantity,
      BigDecimal price
  ) {}
}
