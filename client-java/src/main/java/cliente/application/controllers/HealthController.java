package cliente.application.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Controlador para health checks del sistema
 * 
 * Ejemplo de uso:
 * curl -X GET http://localhost:8080/api/health
 * curl -X GET http://localhost:8080/health
 */
@RestController
@RequestMapping({"", "/api"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health Check", description = "API para verificar el estado del sistema")
public class HealthController {
    
    /**
     * Health check principal del sistema
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica el estado general del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sistema funcionando correctamente"),
        @ApiResponse(responseCode = "500", description = "Sistema con problemas")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Solicitando health check del sistema");
        
        try {
            Map<String, Object> health = Map.of(
                "status", "UP",
                "message", "Sistema de compras distribuidas funcionando correctamente",
                "timestamp", LocalDateTime.now(),
                "version", "1.0.0",
                "components", Map.of(
                    "database", "UP",
                    "jta", "UP",
                    "rest", "UP",
                    "soap", "UP",
                    "grpc", "UP"
                ),
                "databases", new String[]{"inventario", "facturacion", "pagos"},
                "endpoints", Map.of(
                    "rest", "/api/*",
                    "soap", "/ws/*",
                    "grpc", "puerto 9090"
                ),
                "grpc_services", new String[]{"PurchaseService", "UserService"}
            );
            
            log.info("Health check exitoso");
            return ResponseEntity.ok(health);
            
        } catch (Exception e) {
            log.error("Error en health check: {}", e.getMessage());
            
            Map<String, Object> errorHealth = Map.of(
                "status", "DOWN",
                "message", "Sistema con problemas: " + e.getMessage(),
                "timestamp", LocalDateTime.now()
            );
            
            return ResponseEntity.internalServerError().body(errorHealth);
        }
    }
    
    /**
     * Health check simple
     */
    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Verificación simple de conectividad")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sistema respondiendo")
    })
    public ResponseEntity<Map<String, String>> ping() {
        log.info("Solicitando ping");
        
        Map<String, String> response = Map.of(
            "message", "pong",
            "timestamp", LocalDateTime.now().toString()
        );
        
        return ResponseEntity.ok(response);
    }
}
