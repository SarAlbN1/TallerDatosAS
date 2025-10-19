package cliente.application.kafka;

import cliente.application.events.VentaCompletadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class VentaEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void publicarVentaCompletada(VentaCompletadaEvent event) {
        try {
            log.info("📤 Publicando evento de venta completada: {}", event.getFacturaId());
            
            // Determinar qué proveedores están involucrados
            List<String> proveedores = determinarProveedores(event.getProductos());
            
            // Enviar a cada proveedor (point-to-point)
            for (String proveedor : proveedores) {
                String topic = "ventas-proveedor-" + proveedor.toLowerCase();
                kafkaTemplate.send(topic, event.getFacturaId(), event);
                log.info("📦 Enviado a proveedor {} en topic: {}", proveedor, topic);
            }
            
            // Enviar notificación a cliente (fan-out)
            kafkaTemplate.send("notificaciones-clientes", event.getFacturaId(), event);
            log.info("📧 Enviado a notificaciones de clientes");
            
        } catch (Exception e) {
            log.error("❌ Error publicando evento de venta: {}", e.getMessage(), e);
            throw new RuntimeException("Error publicando evento de venta", e);
        }
    }

    private List<String> determinarProveedores(List<VentaCompletadaEvent.ProductoVenta> productos) {
        return productos.stream()
                .map(VentaCompletadaEvent.ProductoVenta::getProveedor)
                .distinct()
                .toList();
    }
}
