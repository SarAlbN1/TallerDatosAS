package proveedor.application.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class ProveedorController {

    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "Proveedor A");
        response.put("timestamp", LocalDateTime.now());
        response.put("kafka-topic", "ventas-proveedor-a");
        response.put("group-id", "proveedor-a-group");
        
        log.info("🏥 Health check - Proveedor A está funcionando");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> info() {
        Map<String, Object> response = new HashMap<>();
        response.put("proveedor", "A");
        response.put("descripcion", "Proveedor de Tecnología (Laptops, PCs)");
        response.put("topics-consumidos", new String[]{"ventas-proveedor-a"});
        response.put("funcionalidades", new String[]{
            "Actualización de inventario",
            "Generación de órdenes de reposición", 
            "Notificaciones al equipo de ventas"
        });
        
        return ResponseEntity.ok(response);
    }
}
