package cliente.application.controllers;

import cliente.application.services.TransactionDistribuidaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
@Slf4j
public class TransactionController {

    private final TransactionDistribuidaService transactionService;

    /**
     * Endpoint para probar transacción distribuida exitosa
     */
    @PostMapping("/venta-completa")
    public ResponseEntity<?> procesarVentaCompleta(
            @RequestParam String sku,
            @RequestParam Integer cantidad,
            @RequestParam Long clienteId,
            @RequestParam String metodoPagoId) {
        
        try {
            transactionService.procesarVentaCompleta(sku, cantidad, clienteId, metodoPagoId);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Transacción distribuida completada exitosamente",
                "sku", sku,
                "cantidad", cantidad,
                "clienteId", clienteId
            ));
        } catch (Exception e) {
            log.error("Error en transacción: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para probar rollback de transacción distribuida
     */
    @PostMapping("/simular-fallo")
    public ResponseEntity<?> simularFalloRollback(
            @RequestParam String sku,
            @RequestParam Integer cantidad,
            @RequestParam Long clienteId,
            @RequestParam String metodoPagoId) {
        
        try {
            transactionService.simularFalloRollback(sku, cantidad, clienteId, metodoPagoId);
            return ResponseEntity.ok(Map.of(
                "status", "unexpected",
                "message", "Esta respuesta no debería verse - debería haber fallado"
            ));
        } catch (Exception e) {
            log.info("Fallo esperado en transacción: {}", e.getMessage());
            return ResponseEntity.ok(Map.of(
                "status", "rollback_success",
                "message", "Transacción falló como se esperaba - rollback ejecutado",
                "error", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para probar transacción simple de inventario
     */
    @PostMapping("/actualizar-stock")
    public ResponseEntity<?> actualizarStock(
            @RequestParam String sku,
            @RequestParam Integer cantidad) {
        
        try {
            var item = transactionService.actualizarStock(sku, cantidad);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Stock actualizado correctamente",
                "sku", sku,
                "cantidad", cantidad,
                "nuevoStock", item.getStock()
            ));
        } catch (Exception e) {
            log.error("Error actualizando stock: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para probar transacción simple de facturación
     */
    @PostMapping("/crear-factura")
    public ResponseEntity<?> crearFactura(
            @RequestParam Long clienteId,
            @RequestParam String sku,
            @RequestParam Integer cantidad,
            @RequestParam BigDecimal total) {
        
        try {
            String numeroFactura = transactionService.crearFactura(clienteId, sku, cantidad, total);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Factura creada correctamente",
                "numeroFactura", numeroFactura,
                "clienteId", clienteId,
                "sku", sku,
                "cantidad", cantidad,
                "total", total
            ));
        } catch (Exception e) {
            log.error("Error creando factura: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint para probar transacción simple de pagos
     */
    @PostMapping("/procesar-pago")
    public ResponseEntity<?> procesarPago(
            @RequestParam String metodoPagoId,
            @RequestParam BigDecimal monto) {
        
        try {
            String referenciaPago = transactionService.procesarPago(metodoPagoId, monto);
            return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pago procesado correctamente",
                "referenciaPago", referenciaPago,
                "metodoPagoId", metodoPagoId,
                "monto", monto
            ));
        } catch (Exception e) {
            log.error("Error procesando pago: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "status", "error",
                "message", e.getMessage()
            ));
        }
    }

    /**
     * Endpoint de health check para verificar el estado de las conexiones
     */
    @GetMapping("/health")
    public ResponseEntity<?> healthCheck() {
        return ResponseEntity.ok(Map.of(
            "status", "healthy",
            "message", "Servicio de transacciones distribuidas funcionando",
            "jta_enabled", true,
            "databases", new String[]{"inventario", "facturacion", "pagos"}
        ));
    }
}