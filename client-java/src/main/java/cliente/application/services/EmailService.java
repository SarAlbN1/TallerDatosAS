package cliente.application.services;

import cliente.application.events.VentaCompletadaEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;

    @Value("${email.from:noreply@tallerdatosas.com}")
    private String fromEmail;

    @Value("${email.from.name:TallerDatosAS}")
    private String fromName;

    public void enviarConfirmacionCompra(VentaCompletadaEvent event) throws MessagingException {
        log.info("📧 Enviando email de confirmación de compra a: {}", event.getClienteEmail());
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            helper.setFrom(fromEmail);
        }
        helper.setTo(event.getClienteEmail());
        helper.setSubject("✅ Confirmación de Compra - Factura #" + event.getFacturaId());
        
        // Crear contexto para el template
        Context context = new Context(Locale.getDefault());
        context.setVariable("event", event);
        context.setVariable("fechaFormateada", event.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        String htmlContent = templateEngine.process("email/confirmacion-compra", context);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        log.info("✅ Email de confirmación enviado a {} para factura {}", event.getClienteEmail(), event.getFacturaId());
    }

    public void enviarNotificacionAdmin(VentaCompletadaEvent event) throws MessagingException {
        log.info("📧 Enviando notificación de nueva venta al administrador");
        
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        
        try {
            helper.setFrom(fromEmail, fromName);
        } catch (UnsupportedEncodingException e) {
            helper.setFrom(fromEmail);
        }
        helper.setTo("admin@tallerdatosas.com"); // Email del administrador
        helper.setSubject("🛒 Nueva Venta Procesada - Factura #" + event.getFacturaId());
        
        // Crear contexto para el template
        Context context = new Context(Locale.getDefault());
        context.setVariable("event", event);
        context.setVariable("fechaFormateada", event.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        
        String htmlContent = templateEngine.process("email/notificacion-admin", context);
        helper.setText(htmlContent, true);
        
        mailSender.send(message);
        log.info("✅ Email de notificación de admin enviado para factura {}", event.getFacturaId());
    }
}