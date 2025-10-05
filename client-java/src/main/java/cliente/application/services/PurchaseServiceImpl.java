package cliente.application.services;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.application.models.inventario.Item;
import cliente.application.repositories.inventario.ItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de compras (RPC/gRPC)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceImpl {
    
    private final ItemRepository itemRepository;
    private final TransactionDistribuidaService transactionService;
    
    /**
     * Procesa una compra completa
     */
    public CheckoutResponse processPurchase(CheckoutRequest request) {
        log.info("Procesando compra para cliente: {} con {} items", 
                request.getClienteId(), request.getItems().size());
        
        try {
            // 1. Validar stock para todos los items
            validateStockForAllItems(request.getItems());
            
            // 2. Calcular totales
            List<CheckoutResponse.CheckoutItemResponse> itemsResponse = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            
            for (CheckoutRequest.CheckoutItem item : request.getItems()) {
                Item product = itemRepository.findBySku(item.getSku())
                    .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getSku()));
                
                BigDecimal precioUnitario = BigDecimal.valueOf(100.0); // Precio ejemplo
                BigDecimal subtotal = precioUnitario.multiply(BigDecimal.valueOf(item.getCantidad()));
                total = total.add(subtotal);
                
                itemsResponse.add(CheckoutResponse.CheckoutItemResponse.builder()
                    .sku(item.getSku())
                    .nombre(product.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(precioUnitario)
                    .subtotal(subtotal)
                    .build());
            }
            
            // 3. Procesar transacción distribuida
            String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String txId = "TX-" + System.currentTimeMillis();
            
            // Procesar cada item individualmente
            for (CheckoutRequest.CheckoutItem item : request.getItems()) {
                transactionService.procesarVentaCompleta(
                    item.getSku(), 
                    item.getCantidad(), 
                    request.getClienteId(), 
                    request.getMetodoPago()
                );
            }
            
            log.info("Compra procesada exitosamente - Order ID: {} - Total: {}", orderId, total);
            
            return CheckoutResponse.builder()
                .orderId(orderId)
                .status("COMPLETED")
                .txId(txId)
                .clienteId(request.getClienteId())
                .total(total)
                .numeroFactura("FACT-" + System.currentTimeMillis())
                .referenciaPago("PAG-" + System.currentTimeMillis())
                .fechaProcesamiento(LocalDateTime.now())
                .items(itemsResponse)
                .build();
                
        } catch (Exception e) {
            log.error("Error procesando compra: {}", e.getMessage());
            
            return CheckoutResponse.builder()
                .orderId("ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .status("FAILED")
                .txId("TX-" + System.currentTimeMillis())
                .clienteId(request.getClienteId())
                .total(BigDecimal.ZERO)
                .fechaProcesamiento(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        }
    }
    
    /**
     * Valida stock para todos los items
     */
    private void validateStockForAllItems(List<CheckoutRequest.CheckoutItem> items) {
        for (CheckoutRequest.CheckoutItem item : items) {
            Item product = itemRepository.findBySku(item.getSku())
                .orElseThrow(() -> new RuntimeException("Producto no encontrado: " + item.getSku()));
            
            if (product.getStock() < item.getCantidad()) {
                throw new RuntimeException(
                    String.format("Stock insuficiente para %s. Disponible: %d, Solicitado: %d", 
                        item.getSku(), product.getStock(), item.getCantidad())
                );
            }
        }
    }
}