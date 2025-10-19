# Configuración de Email para TallerDatosAS

## Opción 1: Mailtrap (Recomendado para Testing)

Mailtrap es un servicio de testing de email que captura todos los emails enviados sin enviarlos realmente.

### Pasos:
1. Ve a [mailtrap.io](https://mailtrap.io) y crea una cuenta gratuita
2. Ve a "Email Testing" > "Inboxes" > "My Inbox"
3. Copia las credenciales SMTP:
   - Host: `sandbox.smtp.mailtrap.io`
   - Port: `2525`
   - Username: (tu username de Mailtrap)
   - Password: (tu password de Mailtrap)

4. Configura las variables de entorno:
```bash
set EMAIL_USERNAME=tu-mailtrap-username
set EMAIL_PASSWORD=tu-mailtrap-password
```

5. O edita directamente el archivo `application-dev.properties`

## Opción 2: Gmail (Para producción)

### Pasos:
1. Habilita la verificación en 2 pasos en tu cuenta de Gmail
2. Genera una contraseña de aplicación:
   - Ve a "Configuración de Google" > "Seguridad" > "Contraseñas de aplicaciones"
   - Genera una nueva contraseña para "Mail"
3. Configura las variables de entorno:
```bash
set EMAIL_USERNAME=tu-email@gmail.com
set EMAIL_PASSWORD=tu-app-password
```

4. O edita el archivo `application.properties` para usar Gmail:
```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=${EMAIL_USERNAME:tu-email@gmail.com}
spring.mail.password=${EMAIL_PASSWORD:tu-app-password}
```

## Verificación

Una vez configurado, puedes probar el sistema:
1. Ejecuta el backend: `mvn spring-boot:run`
2. Haz una compra usando el endpoint `/api/checkout`
3. Verifica que se envíe el email de confirmación

## Logs de Email

Los logs de email aparecerán en la consola del backend con el prefijo `[EMAIL-MDB]` y `📧`.