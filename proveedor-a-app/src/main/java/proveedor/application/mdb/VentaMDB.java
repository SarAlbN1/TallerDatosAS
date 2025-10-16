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

    @KafkaListener(topics = "ventas-proveedor-a", groupId = "proveedor-a-group")
    public void procesarVenta(VentaCompletadaEvent event) {
        try {
            log.info("🏭 PROVEEDOR A - Venta recibida: Factura {} por ${}", 
                    event.getFacturaId(), event.getTotal());
            log.info("📦 Productos: {}", event.getProductos());
            
            // Actualizar inventario del proveedor
            inventarioService.actualizarStock(event.getProductos());
            
            // Generar orden de reposición si es necesario
            if (inventarioService.necesitaReposicion()) {
                log.info("📋 Generando orden de reposición para Proveedor A");
                inventarioService.generarOrdenReposicion(event);
            }
            
            // Notificar al equipo de ventas
            notificacionService.notificarEquipo(event);
            
            log.info("✅ PROVEEDOR A - Venta procesada exitosamente: {}", event.getFacturaId());
            
        } catch (Exception e) {
            log.error("❌ PROVEEDOR A - Error procesando venta {}: {}", 
                     event.getFacturaId(), e.getMessage(), e);
        }
    }
}