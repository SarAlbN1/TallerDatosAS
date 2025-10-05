package cliente.application.grpc;

import cliente.application.services.PurchaseServiceImpl;
import cliente.application.services.TransactionDistribuidaService;
import cliente.grpc.purchase.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio gRPC de compras
 * 
 * Este servicio maneja las operaciones de compra a través de gRPC
 * y orquesta las transacciones distribuidas JTA
 */
@GrpcService
@Service
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceGrpcImpl extends PurchaseServiceGrpc.PurchaseServiceImplBase {
    
    private final PurchaseServiceImpl purchaseService;
    private final TransactionDistribuidaService transactionService;
    
    /**
     * Procesa una compra completa
     */
    @Override
    public void processPurchase(PurchaseRequest request, StreamObserver<PurchaseResponse> responseObserver) {
        log.info("gRPC: Procesando compra - Cliente: {} - Items: {} - Request ID: {}", 
                request.getClienteId(), request.getItemsCount(), request.getRequestId());
        
        try {
            // 1. Validar stock para todos los items
            validateStockForAllItems(request.getItemsList());
            
            // 2. Calcular totales
            List<PurchaseItemResponse> itemsResponse = new ArrayList<>();
            double total = 0.0;
            
            for (PurchaseItem item : request.getItemsList()) {
                double precioUnitario = 100.0; // Precio ejemplo
                double subtotal = precioUnitario * item.getCantidad();
                total += subtotal;
                
                itemsResponse.add(PurchaseItemResponse.newBuilder()
                    .setSku(item.getSku())
                    .setNombre("Producto " + item.getSku()) // En una implementación real, obtendríamos el nombre de la BD
                    .setCantidad(item.getCantidad())
                    .setPrecioUnitario(precioUnitario)
                    .setSubtotal(subtotal)
                    .build());
            }
            
            // 3. Procesar transacción distribuida
            String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String txId = "TX-" + System.currentTimeMillis();
            
            // Procesar cada item individualmente
            for (PurchaseItem item : request.getItemsList()) {
                transactionService.procesarVentaCompleta(
                    item.getSku(), 
                    item.getCantidad(), 
                    request.getClienteId(), 
                    request.getMetodoPago()
                );
            }
            
            // 4. Construir respuesta
            PurchaseResponse response = PurchaseResponse.newBuilder()
                .setOrderId(orderId)
                .setStatus("COMPLETED")
                .setTxId(txId)
                .setClienteId(request.getClienteId())
                .setTotal(total)
                .setNumeroFactura("FACT-" + System.currentTimeMillis())
                .setReferenciaPago("PAG-" + System.currentTimeMillis())
                .setFechaProcesamiento(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .addAllItems(itemsResponse)
                .setMessage("Compra procesada exitosamente")
                .build();
            
            log.info("gRPC: Compra procesada exitosamente - Order ID: {} - Total: {}", orderId, total);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC: Error procesando compra: {}", e.getMessage());
            
            PurchaseResponse errorResponse = PurchaseResponse.newBuilder()
                .setOrderId("ERROR-" + System.currentTimeMillis())
                .setStatus("FAILED")
                .setTxId("TX-ERROR-" + System.currentTimeMillis())
                .setClienteId(request.getClienteId())
                .setTotal(0.0)
                .setFechaProcesamiento(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setMessage("Error procesando compra: " + e.getMessage())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * Confirma una orden
     */
    @Override
    public void confirmOrder(OrderRequest request, StreamObserver<OrderConfirmation> responseObserver) {
        log.info("gRPC: Confirmando orden - Order ID: {} - TX ID: {} - Request ID: {}", 
                request.getOrderId(), request.getTxId(), request.getRequestId());
        
        try {
            // Simular confirmación de orden
            String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            OrderConfirmation response = OrderConfirmation.newBuilder()
                .setOrderId(request.getOrderId())
                .setStatus("CONFIRMED")
                .setTxId(request.getTxId())
                .setConfirmationCode(confirmationCode)
                .setFechaConfirmacion(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setMessage("Orden confirmada exitosamente")
                .build();
            
            log.info("gRPC: Orden confirmada - Order ID: {} - Confirmation Code: {}", 
                    request.getOrderId(), confirmationCode);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC: Error confirmando orden: {}", e.getMessage());
            
            OrderConfirmation errorResponse = OrderConfirmation.newBuilder()
                .setOrderId(request.getOrderId())
                .setStatus("FAILED")
                .setTxId(request.getTxId())
                .setConfirmationCode("ERROR")
                .setFechaConfirmacion(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .setMessage("Error confirmando orden: " + e.getMessage())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * Valida disponibilidad de stock
     */
    @Override
    public void validateStock(StockValidationRequest request, StreamObserver<StockValidationResponse> responseObserver) {
        log.info("gRPC: Validando stock - SKU: {} - Cantidad: {} - Request ID: {}", 
                request.getSku(), request.getCantidad(), request.getRequestId());
        
        try {
            // Simular validación de stock
            boolean disponible = request.getCantidad() <= 100; // Stock simulado
            
            StockValidationResponse response = StockValidationResponse.newBuilder()
                .setDisponible(disponible)
                .setSku(request.getSku())
                .setCantidadSolicitada(request.getCantidad())
                .setStockDisponible(disponible ? request.getCantidad() : 0)
                .setMessage(disponible ? "Stock disponible" : "Stock insuficiente")
                .setRequestId(request.getRequestId())
                .build();
            
            log.info("gRPC: Validación de stock completada - SKU: {} - Disponible: {}", 
                    request.getSku(), disponible);
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC: Error validando stock: {}", e.getMessage());
            
            StockValidationResponse errorResponse = StockValidationResponse.newBuilder()
                .setDisponible(false)
                .setSku(request.getSku())
                .setCantidadSolicitada(request.getCantidad())
                .setStockDisponible(0)
                .setMessage("Error validando stock: " + e.getMessage())
                .setRequestId(request.getRequestId())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * Valida stock para todos los items
     */
    private void validateStockForAllItems(List<PurchaseItem> items) {
        for (PurchaseItem item : items) {
            if (item.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida para SKU: " + item.getSku());
            }
            if (item.getCantidad() > 100) { // Stock simulado
                throw new RuntimeException(
                    String.format("Stock insuficiente para %s. Disponible: 100, Solicitado: %d", 
                        item.getSku(), item.getCantidad())
                );
            }
        }
    }
}
