package cliente.application.ports.in;

import java.math.BigDecimal;

public interface PaymentService {
  String authorize(String userRef, BigDecimal amount, String methodCode);
  void capture(String paymentRef);
  void voidAuthorization(String paymentRef);
}
