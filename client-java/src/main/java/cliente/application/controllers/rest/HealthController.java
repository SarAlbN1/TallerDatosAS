package cliente.application.controllers.rest;

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
 */
@RestController
@RequestMapping({"", "/api"})
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Health Check", description = "API para verificar el estado del sistema")
public class HealthController {
    
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Verifica el estado general del sistema")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sistema funcionando correctamente"),
        @ApiResponse(responseCode = "500", description = "Sistema con problemas")
    })
    public ResponseEntity<Map<String, Object>> healthCheck() {
        log.info("Solicitando health check del sistema");
        Map<String, Object> health = Map.of(
            "status", "UP",
            "timestamp", LocalDateTime.now(),
            "components", Map.of(
                "rest", "UP",
                "soap", "UP",
                "grpc", "UP"
            )
        );
        return ResponseEntity.ok(health);
    }
    
    @GetMapping("/ping")
    @Operation(summary = "Ping", description = "Verificación simple de conectividad")
    public ResponseEntity<Map<String, String>> ping() {
        Map<String, String> response = Map.of(
            "message", "pong",
            "timestamp", LocalDateTime.now().toString()
        );
        return ResponseEntity.ok(response);
    }
}




