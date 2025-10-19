package proveedor.application.mdb;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import proveedor.application.events.VentaCompletadaEvent;
import proveedor.application.services.InventarioService;
import proveedor.application.services.NotificacionService;

@Component
@Slf4j
public class VentaMDB {

    private final InventarioService inventarioService;
    private final NotificacionService notificacionService;

    public VentaMDB(InventarioService inventarioService, NotificacionService notificacionService) {
        this.inventarioService = inventarioService;
        this.notificacionService = notificacionService;
    }

    @KafkaListener(topics = "ventas-proveedor-c", groupId = "proveedor-c-group")
    public void procesarVenta(VentaCompletadaEvent event) {
        log.info("🛒 Proveedor C: Procesando venta - Factura: {}", event.getFacturaId());
        
        try {
            // Actualizar inventario
            inventarioService.actualizarStock(event.getProductos());
            
            // Verificar si necesita reposición
            if (inventarioService.necesitaReposicion()) {
                inventarioService.generarOrdenReposicion(event);
            }
            
            // Enviar notificación al equipo
            notificacionService.notificarEquipo(event);
            
            log.info("✅ Proveedor C: Venta procesada exitosamente - Factura: {}", event.getFacturaId());
            
        } catch (Exception e) {
            log.error("❌ Proveedor C: Error procesando venta - Factura: {} - Error: {}", 
                     event.getFacturaId(), e.getMessage(), e);
        }
    }
}