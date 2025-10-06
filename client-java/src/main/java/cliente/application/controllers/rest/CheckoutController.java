package cliente.application.controllers.rest;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.application.grpc.GrpcClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Checkout", description = "Flujo de compra distribuida (REST->gRPC->JTA)")
public class CheckoutController {

  private final GrpcClientService grpcClient;

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

  @GetMapping("/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().build();
  }
}




