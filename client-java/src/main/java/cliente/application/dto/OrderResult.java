package cliente.application.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record OrderResult(
    String orderId,
    String status,   // "APPROVED" / "REJECTED"
    BigDecimal total,
    List<Line> lines,
    OffsetDateTime createdAt
) {
  public record Line(
      Long productId,
      int quantity,
      BigDecimal price,
      BigDecimal lineTotal
  ) {}
}
