package cliente.application.grpc;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.grpc.purchase.*;
import cliente.grpc.user.*;
import io.grpc.StatusRuntimeException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Cliente gRPC para comunicarse con los servicios gRPC
 * 
 * Este servicio actúa como cliente para los servicios gRPC
 * y proporciona métodos para el CheckoutController
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GrpcClientService {
    
    @GrpcClient("purchase-service")
    private PurchaseServiceGrpc.PurchaseServiceBlockingStub purchaseServiceStub;
    
    @GrpcClient("user-service")
    private UserServiceGrpc.UserServiceBlockingStub userServiceStub;
    
    /**
     * Obtiene un usuario aleatorio a través de gRPC
     */
    public cliente.application.dto.UserResponse getRandomUser() {
        log.info("gRPC Client: Solicitando usuario aleatorio");
        
        try {
            GetRandomUserRequest request = GetRandomUserRequest.newBuilder()
                .setRequestId("REQ-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
            
            GetRandomUserResponse response = userServiceStub.getRandomUser(request);
            
            if ("SUCCESS".equals(response.getStatus())) {
                User user = response.getUser();
                
                cliente.application.dto.UserResponse userDto = cliente.application.dto.UserResponse.builder()
                    .id(user.getId())
                    .nombre(user.getNombre())
                    .email(user.getEmail())
                    .telefono(user.getTelefono())
                    .direccion(user.getDireccion())
                    .ciudad(user.getCiudad())
                    .pais(user.getPais())
                    .build();
                
                log.info("gRPC Client: Usuario obtenido - ID: {} - Nombre: {}", 
                        user.getId(), user.getNombre());
                
                return userDto;
                
            } else {
                log.error("gRPC Client: Error obteniendo usuario: {}", response.getMessage());
                throw new RuntimeException("Error obteniendo usuario: " + response.getMessage());
            }
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Error de comunicación con servicio de usuarios: {}", e.getMessage());
            throw new RuntimeException("Error de comunicación con servicio de usuarios", e);
        }
    }
    
    /**
     * Procesa una compra a través de gRPC
     */
    public CheckoutResponse processPurchase(CheckoutRequest request) {
        log.info("gRPC Client: Procesando compra - Cliente: {} - Items: {}", 
                request.getClienteId(), request.getItems().size());
        
        try {
            // Construir request gRPC
            List<PurchaseItem> items = new ArrayList<>();
            for (CheckoutRequest.CheckoutItem item : request.getItems()) {
                items.add(PurchaseItem.newBuilder()
                    .setSku(item.getSku())
                    .setCantidad(item.getCantidad())
                    .setPrecioUnitario(100.0) // Precio ejemplo
                    .build());
            }
            
            PurchaseRequest grpcRequest = PurchaseRequest.newBuilder()
                .addAllItems(items)
                .setClienteId(request.getClienteId())
                .setMetodoPago(request.getMetodoPago())
                .setRequestId("REQ-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
            
            // Llamar al servicio gRPC
            PurchaseResponse grpcResponse = purchaseServiceStub.processPurchase(grpcRequest);
            
            // Mapear respuesta gRPC a DTO
            List<CheckoutResponse.CheckoutItemResponse> itemsResponse = new ArrayList<>();
            for (PurchaseItemResponse item : grpcResponse.getItemsList()) {
                itemsResponse.add(CheckoutResponse.CheckoutItemResponse.builder()
                    .sku(item.getSku())
                    .nombre(item.getNombre())
                    .cantidad(item.getCantidad())
                    .precioUnitario(java.math.BigDecimal.valueOf(item.getPrecioUnitario()))
                    .subtotal(java.math.BigDecimal.valueOf(item.getSubtotal()))
                    .build());
            }
            
            CheckoutResponse response = CheckoutResponse.builder()
                .orderId(grpcResponse.getOrderId())
                .status(grpcResponse.getStatus())
                .txId(grpcResponse.getTxId())
                .clienteId(grpcResponse.getClienteId())
                .total(java.math.BigDecimal.valueOf(grpcResponse.getTotal()))
                .numeroFactura(grpcResponse.getNumeroFactura())
                .referenciaPago(grpcResponse.getReferenciaPago())
                .fechaProcesamiento(LocalDateTime.parse(grpcResponse.getFechaProcesamiento()))
                .items(itemsResponse)
                .build();
            
            log.info("gRPC Client: Compra procesada - Order ID: {} - Status: {} - Total: {}", 
                    response.getOrderId(), response.getStatus(), response.getTotal());
            
            return response;
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Error de comunicación con servicio de compras: {}", e.getMessage());
            
            return CheckoutResponse.builder()
                .orderId("ERROR-" + System.currentTimeMillis())
                .status("FAILED")
                .txId("TX-ERROR-" + System.currentTimeMillis())
                .clienteId(request.getClienteId())
                .total(java.math.BigDecimal.ZERO)
                .fechaProcesamiento(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        }
    }
    
    /**
     * Valida stock a través de gRPC
     */
    public boolean validateStock(String sku, Integer cantidad) {
        log.info("gRPC Client: Validando stock - SKU: {} - Cantidad: {}", sku, cantidad);
        
        try {
            StockValidationRequest request = StockValidationRequest.newBuilder()
                .setSku(sku)
                .setCantidad(cantidad)
                .setRequestId("REQ-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
            
            StockValidationResponse response = purchaseServiceStub.validateStock(request);
            
            log.info("gRPC Client: Validación de stock - SKU: {} - Disponible: {}", 
                    sku, response.getDisponible());
            
            return response.getDisponible();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Error validando stock: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Confirma una orden a través de gRPC
     */
    public String confirmOrder(String orderId, String txId, Long clienteId) {
        log.info("gRPC Client: Confirmando orden - Order ID: {} - TX ID: {}", orderId, txId);
        
        try {
            OrderRequest request = OrderRequest.newBuilder()
                .setOrderId(orderId)
                .setTxId(txId)
                .setClienteId(clienteId)
                .setRequestId("REQ-" + UUID.randomUUID().toString().substring(0, 8))
                .build();
            
            OrderConfirmation response = purchaseServiceStub.confirmOrder(request);
            
            log.info("gRPC Client: Orden confirmada - Order ID: {} - Confirmation Code: {}", 
                    orderId, response.getConfirmationCode());
            
            return response.getConfirmationCode();
            
        } catch (StatusRuntimeException e) {
            log.error("gRPC Client: Error confirmando orden: {}", e.getMessage());
            return "ERROR";
        }
    }
}
