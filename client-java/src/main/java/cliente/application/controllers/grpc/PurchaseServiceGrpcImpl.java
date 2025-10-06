package cliente.application.controllers.grpc;

import cliente.application.services.PurchaseServiceImpl;
import cliente.grpc.purchase.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio gRPC de compras
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class PurchaseServiceGrpcImpl extends PurchaseServiceGrpc.PurchaseServiceImplBase {
    
    private final PurchaseServiceImpl purchaseService;
    
    @Override
    public void processPurchase(PurchaseRequest request, StreamObserver<PurchaseResponse> responseObserver) {
        log.info("gRPC: Procesando compra - Cliente: {} - Items: {} - Request ID: {}", 
                request.getClienteId(), request.getItemsCount(), request.getRequestId());
        try {
            // 1) Validación simple de stock
            validateStockForAllItems(request.getItemsList());

            List<PurchaseItemResponse> itemsResponse = new ArrayList<>();
            double total = 0.0;
            for (PurchaseItem item : request.getItemsList()) {
                double precioUnitario = 100.0;
                double subtotal = precioUnitario * item.getCantidad();
                total += subtotal;
                itemsResponse.add(PurchaseItemResponse.newBuilder()
                    .setSku(item.getSku())
                    .setNombre("Producto " + item.getSku())
                    .setCantidad(item.getCantidad())
                    .setPrecioUnitario(precioUnitario)
                    .setSubtotal(subtotal)
                    .build());
            }

            String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String txId = "TX-" + System.currentTimeMillis();

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

    @Override
    public void confirmOrder(OrderRequest request, StreamObserver<OrderConfirmation> responseObserver) {
        log.info("gRPC: Confirmando orden - Order ID: {} - TX ID: {} - Request ID: {}", 
                request.getOrderId(), request.getTxId(), request.getRequestId());
        String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        OrderConfirmation response = OrderConfirmation.newBuilder()
            .setOrderId(request.getOrderId())
            .setStatus("CONFIRMED")
            .setTxId(request.getTxId())
            .setConfirmationCode(confirmationCode)
            .setFechaConfirmacion(LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .setMessage("Orden confirmada exitosamente")
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void validateStock(StockValidationRequest request, StreamObserver<StockValidationResponse> responseObserver) {
        boolean disponible = request.getCantidad() <= 100;
        StockValidationResponse response = StockValidationResponse.newBuilder()
            .setDisponible(disponible)
            .setSku(request.getSku())
            .setCantidadSolicitada(request.getCantidad())
            .setStockDisponible(disponible ? request.getCantidad() : 0)
            .setMessage(disponible ? "Stock disponible" : "Stock insuficiente")
            .setRequestId(request.getRequestId())
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    private void validateStockForAllItems(java.util.List<PurchaseItem> items) {
        for (PurchaseItem item : items) {
            if (item.getCantidad() <= 0) {
                throw new RuntimeException("Cantidad inválida para SKU: " + item.getSku());
            }
            if (item.getCantidad() > 100) {
                throw new RuntimeException("Stock insuficiente para " + item.getSku());
            }
        }
    }
}




