package cliente.application.services;

import cliente.application.models.inventario.Item;
import cliente.application.models.facturacion.Cliente;
import cliente.application.models.facturacion.Factura;
import cliente.application.models.facturacion.FacturaDetalle;
import cliente.application.models.pagos.MetodoPago;
import cliente.application.models.pagos.Pago;
import cliente.application.repositories.inventario.ItemRepository;
import cliente.application.repositories.facturacion.ClienteRepository;
import cliente.application.repositories.facturacion.FacturaRepository;
import cliente.application.repositories.pagos.MetodoPagoRepository;
import cliente.application.repositories.pagos.PagoRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio que demuestra transacciones distribuidas JTA
 * entre múltiples bases de datos (inventario, facturación, pagos)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class TransactionDistribuidaService {

    private final ItemRepository itemRepository;
    private final ClienteRepository clienteRepository;
    private final FacturaRepository facturaRepository;
    private final MetodoPagoRepository metodoPagoRepository;
    private final PagoRepository pagoRepository;

    /**
     * Ejemplo de transacción distribuida que abarca múltiples datasources
     * Si alguna operación falla, todas las operaciones en todas las bases se revierten
     */
    @Transactional(rollbackFor = Exception.class)
    public String procesarVentaCompleta(String sku, Integer cantidad, Long clienteId, String metodoPagoCodigo) {
        try {
            log.info("Iniciando transacción distribuida para venta de {} unidades de {}", cantidad, sku);
            
            // 1. Verificar y actualizar stock en base inventario
            actualizarStock(sku, cantidad);
            BigDecimal precioUnitario = BigDecimal.valueOf(100.0); // Precio ejemplo
            BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            
            // 2. Crear factura en base facturación
            String numeroFactura = crearFactura(clienteId, sku, cantidad, precioUnitario);
            
            // 3. Procesar pago en base pagos
            String referenciaPago = procesarPago(metodoPagoCodigo, total);
            
            log.info("Transacción distribuida completada exitosamente - Factura: {} - Pago: {}", numeroFactura, referenciaPago);
            return "Venta procesada - Factura: " + numeroFactura + " - Pago: " + referenciaPago;
            
        } catch (Exception e) {
            log.error("Error en transacción distribuida, ejecutando rollback: {}", e.getMessage());
            throw e; // Provoca rollback automático en todas las bases
        }
    }
    
    /**
     * Actualiza el stock de un item (debe ejecutarse dentro de una transacción)
     */
    @Transactional(rollbackFor = Exception.class, propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    public Item actualizarStock(String sku, Integer cantidad) {
        log.info("Actualizando stock para SKU: {} - Cantidad: {}", sku, cantidad);
        
        Item item = itemRepository.findBySku(sku)
            .orElseThrow(() -> new RuntimeException("Producto no encontrado con SKU: " + sku));
        
        if (item.getStock() < cantidad) {
            throw new RuntimeException("Stock insuficiente. Disponible: " + item.getStock() + ", Solicitado: " + cantidad);
        }
        
        item.setStock(item.getStock() - cantidad);
        return itemRepository.save(item);
    }
    
    /**
     * Crea una factura (debe ejecutarse dentro de una transacción)
     */
    @Transactional(rollbackFor = Exception.class, propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    public String crearFactura(Long clienteId, String sku, Integer cantidad, BigDecimal precioUnitario) {
        log.info("Creando factura para cliente: {} - SKU: {} - Cantidad: {}", clienteId, sku, cantidad);
        
        Cliente cliente = clienteRepository.findById(clienteId)
            .orElseThrow(() -> new RuntimeException("Cliente no encontrado con ID: " + clienteId));
        
        String numeroFactura = "FACT-" + System.currentTimeMillis();
        BigDecimal total = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
        
        Factura factura = Factura.builder()
            .numero(numeroFactura)
            .fecha(LocalDate.now())
            .cliente(cliente)
            .total(total)
            .build();
            
        facturaRepository.save(factura);
        
        // Crear detalle de factura
        FacturaDetalle detalle = FacturaDetalle.builder()
            .factura(factura)
            .concepto("Venta de " + sku)
            .cantidad(cantidad)
            .precioUnitario(precioUnitario)
            .build();
            
        // No necesitamos repository para detalle porque cascade=ALL en Factura lo maneja
        factura.setDetalles(List.of(detalle));
        
        return numeroFactura;
    }
    
    /**
     * Procesa un pago (debe ejecutarse dentro de una transacción)
     */
    @Transactional(rollbackFor = Exception.class, propagation = org.springframework.transaction.annotation.Propagation.REQUIRED)
    public String procesarPago(String metodoPagoCodigo, BigDecimal total) {
        log.info("Procesando pago con método: {} - Total: {}", metodoPagoCodigo, total);
        
        MetodoPago metodoPago = metodoPagoRepository.findByCodigo(metodoPagoCodigo)
            .orElseThrow(() -> new RuntimeException("Método de pago no encontrado: " + metodoPagoCodigo));
        
        String referenciaPago = "PAG-" + System.currentTimeMillis();
        
        Pago pago = Pago.builder()
            .referencia(referenciaPago)
            .fecha(LocalDateTime.now())
            .importe(total)
            .moneda("EUR")
            .metodo(metodoPago)
            .estado(Pago.EstadoPago.CONFIRMADO)
            .build();
            
        pagoRepository.save(pago);
        
        return referenciaPago;
    }
    
    /**
     * Método que simula un fallo para probar rollback
     */
    @Transactional(rollbackFor = Exception.class)
    public void simularFalloRollback(String sku, Integer cantidad, Long clienteId, String metodoPagoCodigo) {
        log.info("Simulando fallo para probar rollback");
        
        // 1. Actualizar stock (se ejecuta)
        actualizarStock(sku, cantidad);
        log.info("Stock actualizado - esto debería revertirse");
        
        // 2. Crear factura (se ejecuta)
        crearFactura(clienteId, sku, cantidad, BigDecimal.valueOf(100.0));
        log.info("Factura creada - esto debería revertirse");
        
        // 3. Falla intencionalmente antes del pago
        throw new RuntimeException("Fallo simulado - todas las operaciones anteriores deben revertirse");
    }
}