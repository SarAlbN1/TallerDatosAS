package cliente.application.services;

import cliente.application.exceptions.BusinessException;
import cliente.application.models.pagos.MetodoPago;
import cliente.application.models.pagos.Pago;
import cliente.application.models.pagos.Pago.EstadoPago;
import cliente.application.ports.in.PaymentService;
import cliente.application.repositories.pagos.MetodoPagoRepository;
import cliente.application.repositories.pagos.PagoRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PaymentServiceImpl implements PaymentService {

  private final MetodoPagoRepository metodos;
  private final PagoRepository pagos;

  public PaymentServiceImpl(MetodoPagoRepository metodos, PagoRepository pagos) {
    this.metodos = metodos;
    this.pagos = pagos;
  }

  @Override
  @Transactional
  public String authorize(String userRef, BigDecimal amount, String methodCode) {
    MetodoPago metodo = metodos.findByCodigo(methodCode)
        .orElseThrow(() -> new BusinessException("PAYMENT_METHOD_NOT_FOUND", "Método de pago inválido: " + methodCode));

    if (amount == null || amount.signum() <= 0) {
      throw new BusinessException("AMOUNT_INVALID", "Monto inválido");
    }

    String ref = "PAG-" + UUID.randomUUID();
    Pago pago = Pago.builder()
        .referencia(ref)
        .fecha(LocalDateTime.now())
        .importe(amount)
        .moneda("EUR")
        .metodo(metodo)
        // AUTHORIZED  ≈  PENDIENTE
        .estado(EstadoPago.PENDIENTE)
        .build();

    pagos.save(pago);
    return ref;
  }

  @Override
  @Transactional
  public void capture(String paymentRef) {
    pagos.findByReferencia(paymentRef).ifPresent(p -> {
      // CAPTURED  ≈  CONFIRMADO
      if (p.getEstado() != EstadoPago.CONFIRMADO) {
        p.setEstado(EstadoPago.CONFIRMADO);
        pagos.save(p);
      }
    });
  }

  @Override
  @Transactional
  public void voidAuthorization(String paymentRef) {
    pagos.findByReferencia(paymentRef).ifPresent(p -> {
      // VOID  ≈  REEMBOLSADO (o FALLIDO si lo prefieres)
      p.setEstado(EstadoPago.REEMBOLSADO);
      pagos.save(p);
    });
  }
}
