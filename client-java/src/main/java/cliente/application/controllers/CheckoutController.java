package cliente.application.controllers;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.application.dto.UserResponse;
import cliente.application.grpc.GrpcClientService;
import cliente.application.services.PurchaseServiceImpl;
import cliente.application.services.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador REST para proceso de checkout
 * 
 * Ejemplo de uso:
 * curl -X POST http://localhost:8080/api/checkout \
 *   -H "Content-Type: application/json" \
 *   -d '{
 *     "items": [
 *       {"sku": "PROD001", "cantidad": 2},
 *       {"sku": "PROD002", "cantidad": 1}
 *     ],
 *     "clienteId": 1,
 *     "metodoPago": "TARJETA"
 *   }'
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Checkout Management", description = "API para proceso de checkout y compras distribuidas")
public class CheckoutController {
    
    private final PurchaseServiceImpl purchaseService;
    private final UserServiceImpl userService;
    private final GrpcClientService grpcClientService;
    
    /**
     * Procesa el checkout completo de una compra
     */
    @PostMapping("/checkout")
    @Operation(summary = "Procesar checkout", description = "Procesa una compra completa con transacciones distribuidas")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Checkout procesado exitosamente"),
        @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o stock insuficiente"),
        @ApiResponse(responseCode = "500", description = "Error interno del servidor")
    })
    public ResponseEntity<CheckoutResponse> processCheckout(
            @Valid @RequestBody CheckoutRequest request) {
        log.info("Iniciando checkout para cliente: {} con {} items", 
                request.getClienteId(), request.getItems().size());
        
        try {
            // 1. Obtener usuario aleatorio a través de gRPC
            UserResponse usuario = grpcClientService.getRandomUser();
            log.info("Usuario obtenido via gRPC: {} - ID: {}", usuario.getNombre(), usuario.getId());
            
            // 2. Procesar compra a través de gRPC
            CheckoutResponse response = grpcClientService.processPurchase(request);
            
            log.info("Checkout completado - Order ID: {} - Status: {} - Total: {}", 
                    response.getOrderId(), response.getStatus(), response.getTotal());
            
            if ("COMPLETED".equals(response.getStatus())) {
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            log.error("Error procesando checkout: {}", e.getMessage());
            
            CheckoutResponse errorResponse = CheckoutResponse.builder()
                .orderId("ERROR-" + System.currentTimeMillis())
                .status("FAILED")
                .txId("TX-ERROR-" + System.currentTimeMillis())
                .clienteId(request.getClienteId())
                .total(java.math.BigDecimal.ZERO)
                .fechaProcesamiento(java.time.LocalDateTime.now())
                .items(java.util.Collections.emptyList())
                .build();
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
    
}
