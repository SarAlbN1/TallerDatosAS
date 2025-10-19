package proveedor.application.services;

import proveedor.application.events.VentaCompletadaEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificacionService {

    public void notificarEquipo(VentaCompletadaEvent event) {
        log.info("📧 Enviando notificación al equipo de ventas para factura: {}", event.getFacturaId());
        
        // Simular envío de notificación
        String mensaje = String.format(
            "Nueva venta procesada:\n" +
            "Factura: %s\n" +
            "Cliente: %s (%s)\n" +
            "Total: %s\n" +
            "Productos: %d\n" +
            "Fecha: %s",
            event.getFacturaId(),
            event.getClienteId(),
            event.getClienteEmail(),
            event.getTotal(),
            event.getProductos().size(),
            event.getFecha()
        );
        
        log.info("📨 Mensaje enviado al equipo:\n{}", mensaje);
        
        // En un sistema real, aquí enviarías:
        // - Email al equipo de ventas
        // - Notificación push
        // - Mensaje a Slack/Teams
        // - Actualización de dashboard
    }
}
