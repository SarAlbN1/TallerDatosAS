package cliente.application.services;

import cliente.application.models.inventario.Item;
import cliente.application.models.facturacion.Cliente;
import cliente.application.models.facturacion.Factura;
import cliente.application.models.facturacion.FacturaDetalle;
import cliente.application.models.pagos.MetodoPago;
import cliente.application.models.pagos.Pago;
import cliente.application.models.pagos.Pago.EstadoPago;
import cliente.application.repositories.inventario.ItemRepository;
import cliente.application.repositories.facturacion.ClienteRepository;
import cliente.application.repositories.facturacion.FacturaRepository;
import cliente.application.repositories.pagos.MetodoPagoRepository;
import cliente.application.repositories.pagos.PagoRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutCoordinatorService {

    private final ItemRepository itemRepository;
    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PagoRepository pagoRepository;

    @Transactional
    public String procesarVentaDemo(String sku, Integer cantidad, Long clienteId, String metodoPagoCodigo) {
        log.info("Iniciando 2PC demo: {} unidades de {}", cantidad, sku);

        // 1) Inventario
        var item = itemRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con SKU: " + sku));
        if (item.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + item.getStock() + ", Solicitado: " + cantidad);
        }
        item.setStock(item.getStock() - cantidad);
        itemRepository.save(item);

        BigDecimal precioUnitario = BigDecimal.valueOf(100.00);
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));

        // 2) Facturación
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
        String numeroFactura = "FACT-" + System.currentTimeMillis();

        Factura factura = Factura.builder()
            .numero(numeroFactura)
            .fecha(LocalDate.now())
            .cliente(cliente)
            .total(total)
            .build();

        FacturaDetalle detalle = FacturaDetalle.builder()
            .factura(factura)
            .concepto("Venta de " + sku)
            .cantidad(cantidad)
            .precioUnitario(precioUnitario)
            .build();

        factura.setDetalles(List.of(detalle));
        facturaRepository.save(factura);

        // 3) Pago
        MetodoPago metodoPago = metodoPagoRepository.findByCodigo(metodoPagoCodigo)
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + metodoPagoCodigo));

        String referenciaPago = "PAG-" + System.currentTimeMillis();
        Pago pago = Pago.builder()
            .referencia(referenciaPago)
            .fecha(LocalDateTime.now())
            .importe(total)
            .moneda("EUR")
            .metodo(metodoPago)
            // CAPTURED  ≈  CONFIRMADO
            .estado(EstadoPago.CONFIRMADO)
            .build();

        pagoRepository.save(pago);

        log.info("2PC demo OK - Factura: {} - Pago: {}", numeroFactura, referenciaPago);
        return "OK - Factura: " + numeroFactura + " - Pago: " + referenciaPago;
    }

    @Transactional
    public void simularFalloRollback(String sku, Integer cantidad, Long clienteId, String metodoPagoCodigo) {
        procesarVentaDemo(sku, cantidad, clienteId, metodoPagoCodigo);
        throw new RuntimeException("Fallo simulado - rollback global");
    }
}
