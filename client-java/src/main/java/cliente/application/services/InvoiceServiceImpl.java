package cliente.application.services;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;
import cliente.application.exceptions.BusinessException;
import cliente.application.models.facturacion.Cliente;
import cliente.application.models.facturacion.Factura;
import cliente.application.models.facturacion.FacturaDetalle;
import cliente.application.ports.in.InvoiceService;
import cliente.application.repositories.facturacion.ClienteRepository;
import cliente.application.repositories.facturacion.FacturaRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
public class InvoiceServiceImpl implements InvoiceService {

  private final FacturaRepository facturas;
  private final ClienteRepository clientes;

  public InvoiceServiceImpl(FacturaRepository facturas, ClienteRepository clientes) {
    this.facturas = facturas;
    this.clientes = clientes;
  }

  @Override
  @Transactional
  public OrderResult generateInvoice(String orderId, OrderCommand cmd, String paymentRef) {
    // 1) Cliente
    Cliente cliente = clientes.findById(cmd.userId())
        .orElseThrow(() -> new BusinessException("CLIENT_NOT_FOUND", "Cliente no encontrado: " + cmd.userId()));

    // 2) Total a partir del comando
    BigDecimal total = cmd.items().stream()
        .map(i -> i.price().multiply(BigDecimal.valueOf(i.quantity())))
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    // 3) Factura + detalles
    Factura factura = Factura.builder()
        .numero("FACT-" + System.currentTimeMillis())
        .fecha(LocalDate.now())
        .cliente(cliente)
        .total(total)
        .build();

    factura.setDetalles(new ArrayList<>());
    cmd.items().forEach(i -> {
      FacturaDetalle det = FacturaDetalle.builder()
          .factura(factura)
          .concepto("Venta de producto " + i.productId())
          .cantidad(i.quantity())
          .precioUnitario(i.price())
          .build();
      factura.getDetalles().add(det);
    });

    facturas.save(factura);

    // 4) DTO de salida
    var lines = cmd.items().stream().map(i ->
        new OrderResult.Line(
            i.productId(),
            i.quantity(),
            i.price(),
            i.price().multiply(BigDecimal.valueOf(i.quantity()))
        )
    ).collect(Collectors.toList());

    return new OrderResult(
        orderId,
        "APPROVED",
        total,
        lines,
        OffsetDateTime.now()
    );
  }
}
