package cliente.application.controllers;

import cliente.application.dto.OrderCommand;
import cliente.application.dto.OrderResult;
import cliente.application.ports.in.PurchaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

  private final PurchaseService purchaseService;

  @PostMapping
  public ResponseEntity<OrderResult> processOrder(@RequestBody OrderCommand cmd) {
    OrderResult result = purchaseService.process(cmd);
    return ResponseEntity.ok(result);
  }

  @GetMapping("/health")
  public ResponseEntity<?> health() {
    return ResponseEntity.ok().build();
  }
}
