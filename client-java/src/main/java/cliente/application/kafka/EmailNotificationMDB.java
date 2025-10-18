package cliente.application.kafka;

import cliente.application.events.VentaCompletadaEvent;
import cliente.application.services.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailNotificationMDB {

    private final EmailService emailService;

    @KafkaListener(
        topics = "notificaciones-clientes",
        groupId = "email-notification-group",
        containerFactory = "kafkaListenerContainerFactory"
    )
    public void procesarNotificacionCliente(
            @Payload VentaCompletadaEvent event,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            @Header(KafkaHeaders.RECEIVED_PARTITION) int partition,
            @Header(KafkaHeaders.OFFSET) long offset) {
        
        try {
            log.info(" [EMAIL-MDB] Procesando notificación de email para cliente: {}", event.getClienteEmail());
            log.info(" [EMAIL-MDB] Topic: {}, Partition: {}, Offset: {}", topic, partition, offset);
            log.info(" [EMAIL-MDB] Factura: {}, Total: ${}", event.getFacturaId(), event.getTotal());
            
            // Enviar email de confirmación al cliente
            emailService.enviarConfirmacionCompra(event);
            
            // Enviar notificación al administrador
            emailService.enviarNotificacionAdmin(event);
            
            log.info(" [EMAIL-MDB] Emails enviados exitosamente para factura: {}", event.getFacturaId());
            
        } catch (Exception e) {
            log.error(" [EMAIL-MDB] Error procesando notificación de email para factura {}: {}", 
                     event.getFacturaId(), e.getMessage(), e);
            
            // En un sistema de producción, aquí implementarías:
            // - Dead Letter Queue
            // - Retry mechanism
            // - Alertas al equipo de soporte
            throw new RuntimeException("Error procesando notificación de email", e);
        }
    }
}
