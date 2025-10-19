package cliente.application.controllers.rest;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.application.events.VentaCompletadaEvent;
import cliente.application.grpc.GrpcClientService;
import cliente.application.kafka.VentaEventProducer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Checkout", description = "Flujo de compra distribuida (REST->gRPC->JTA)")
public class CheckoutController {

  private final GrpcClientService grpcClient;
  private final VentaEventProducer ventaEventProducer;

  /**
   * curl -X POST http://localhost:8080/api/checkout \
   *  -H "Content-Type: application/json" \
   *  -d '{"clienteId":1,"metodoPago":"TARJETA","items":[{"sku":"SKU-1","cantidad":2}]}'
   */
  @PostMapping
  @Operation(summary = "Procesar checkout", description = "Ejecuta el flujo completo llamando gRPC de compras")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Compra procesada"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
      @ApiResponse(responseCode = "500", description = "Error interno")
  })
  public ResponseEntity<CheckoutResponse> process(@Valid @RequestBody CheckoutRequest request) {
    log.info("Checkout solicitado - clienteId={}, items={}", request.getClienteId(), request.getItems().size());
    try {
      // Si no viene clienteId, obtenemos un usuario aleatorio para continuar
      if (request.getClienteId() == null) {
        var user = grpcClient.getRandomUser();
        log.info("Usuario aleatorio resuelto para checkout: id={} nombre={}", user.getId(), user.getNombre());
        request.setClienteId(user.getId());
      }

      CheckoutResponse response = grpcClient.processPurchase(request);
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error en checkout: {}", e.getMessage());
      return ResponseEntity.internalServerError().build();
    }
  }

  /**
   * Endpoint simplificado para el frontend
   * Acepta datos del frontend y los convierte al formato interno
   */
  @PostMapping("/simple")
  @Operation(summary = "Procesar checkout simple", description = "Endpoint simplificado para el frontend")
  @ApiResponses({
      @ApiResponse(responseCode = "200", description = "Compra procesada"),
      @ApiResponse(responseCode = "400", description = "Solicitud inválida"),
      @ApiResponse(responseCode = "500", description = "Error interno")
  })
  public ResponseEntity<CheckoutResponse> processSimple(@RequestBody SimpleCheckoutRequest request) {
    log.info("Checkout simple solicitado - productoId={}, cliente={}, cantidad={}", 
             request.getProductId(), request.getCustomerName(), request.getQuantity());
    
    try {
      // Crear respuesta de éxito simulada para testing
      String orderId = "ORDER-" + System.currentTimeMillis();
      String txId = "TX-" + System.currentTimeMillis();
      
      CheckoutResponse response = CheckoutResponse.builder()
          .orderId(orderId)
          .status("SUCCESS")
          .txId(txId)
          .total(BigDecimal.valueOf(request.getTotalPrice()))
          .fechaProcesamiento(LocalDateTime.now())
          .items(List.of(CheckoutResponse.CheckoutItemResponse.builder()
              .sku("PROD-" + request.getProductId())
              .cantidad(request.getQuantity())
              .precioUnitario(BigDecimal.valueOf(request.getTotalPrice()))
              .subtotal(BigDecimal.valueOf(request.getTotalPrice() * request.getQuantity()))
              .build()))
          .build();
      
      // Enviar notificación por email
      VentaCompletadaEvent.ProductoVenta producto = VentaCompletadaEvent.ProductoVenta.builder()
          .sku("PROD-" + request.getProductId())
          .nombre("Producto " + request.getProductId())
          .cantidad(request.getQuantity())
          .precio(BigDecimal.valueOf(request.getTotalPrice()))
          .proveedor("TechCorp")
          .build();
      
      VentaCompletadaEvent event = VentaCompletadaEvent.builder()
          .facturaId(orderId)
          .clienteEmail(request.getCustomerEmail())
          .productos(List.of(producto))
          .total(BigDecimal.valueOf(request.getTotalPrice()))
          .fecha(LocalDateTime.now())
          .metodoPago("TARJETA")
          .referenciaPago(txId)
          .build();
      
      log.info("Enviando notificación por email para orden: {}", orderId);
      ventaEventProducer.publicarVentaCompletada(event);
      
      log.info("Checkout simple procesado exitosamente - orderId={}", response.getOrderId());
      return ResponseEntity.ok(response);
    } catch (Exception e) {
      log.error("Error en checkout simple: {}", e.getMessage(), e);
      return ResponseEntity.internalServerError().build();
    }
  }

  @GetMapping("/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().build();
  }
  
  // DTO para el frontend
  @lombok.Data
  @lombok.NoArgsConstructor
  @lombok.AllArgsConstructor
  public static class SimpleCheckoutRequest {
    private Long productId;
    private String customerName;
    private String customerEmail;
    private Integer quantity;
    private Double totalPrice;
  }
}




