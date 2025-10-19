package cliente.application.controllers.soap;

import cliente.application.dto.UserResponse;
import cliente.application.models.usuarios.Usuario;
import cliente.application.services.usuarios.UsuarioService;
import cliente.soap.users.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Endpoint SOAP para servicios de usuario
 * WSDL: http://localhost:8080/ws/users.wsdl
 */
@Endpoint
@RequiredArgsConstructor
@Slf4j
public class UserServiceEndpoint {
    
    private static final String NAMESPACE_URI = "http://cliente.com/users";
    
    private final UsuarioService userService;
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetRandomUserRequest")
    @ResponsePayload
    public GetRandomUserResponse getRandomUser(@RequestPayload GetRandomUserRequest request) {
        log.info("SOAP: Solicitando usuario aleatorio - Request ID: {}", request.getRequestId());
        try {
            java.util.List<Usuario> all = userService.getAllUsuarios();
            if (all.isEmpty()) throw new RuntimeException("No hay usuarios disponibles");
            Usuario any = all.get((int)(System.currentTimeMillis() % all.size()));
            var dp = any.getDatosPersonales();
            String nombreCompleto = dp != null ? (dp.getNombre() + (dp.getApellido() != null ? (" " + dp.getApellido()) : "")) : any.getUsername();
            String telefono = dp != null ? dp.getTelefono() : null;
            String direccion = dp != null ? dp.getDireccion() : null;
            String ciudad = dp != null ? dp.getCiudad() : null;
            String pais = dp != null ? dp.getPais() : null;
            UserResponse userDto = UserResponse.builder()
                .id(any.getId())
                .nombre(nombreCompleto)
                .email(any.getEmail())
                .telefono(telefono)
                .direccion(direccion)
                .ciudad(ciudad)
                .pais(pais)
                .build();
            User user = new User();
            user.setId(userDto.getId());
            user.setNombre(userDto.getNombre());
            user.setEmail(userDto.getEmail());
            user.setTelefono(userDto.getTelefono());
            user.setDireccion(userDto.getDireccion());
            user.setCiudad(userDto.getCiudad());
            user.setPais(userDto.getPais());
            
            GetRandomUserResponse response = new GetRandomUserResponse();
            response.setUser(user);
            response.setRequestId(request.getRequestId());
            response.setStatus("SUCCESS");
            return response;
        } catch (Exception e) {
            log.error("SOAP: Error obteniendo usuario aleatorio: {}", e.getMessage());
            GetRandomUserResponse response = new GetRandomUserResponse();
            response.setRequestId(request.getRequestId());
            response.setStatus("ERROR");
            return response;
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidatePaymentRequest")
    @ResponsePayload
    public ValidatePaymentResponse validatePayment(@RequestPayload ValidatePaymentRequest request) {
        log.info("SOAP: Validando pago - Método: {} - Monto: {} - Request ID: {}", 
                request.getPaymentMethod(), request.getAmount(), request.getRequestId());
        try {
            boolean isValid = validatePaymentMethod(request.getPaymentMethod(), request.getAmount());
            ValidatePaymentResponse response = new ValidatePaymentResponse();
            response.setIsValid(isValid);
            response.setRequestId(request.getRequestId());
            response.setStatus("SUCCESS");
            response.setMessage(isValid ? "Pago válido" : "Pago inválido");
            return response;
        } catch (Exception e) {
            log.error("SOAP: Error validando pago: {}", e.getMessage());
            ValidatePaymentResponse response = new ValidatePaymentResponse();
            response.setIsValid(false);
            response.setRequestId(request.getRequestId());
            response.setStatus("ERROR");
            response.setMessage("Error validando pago: " + e.getMessage());
            return response;
        }
    }
    
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetPaymentMethodsRequest")
    @ResponsePayload
    public GetPaymentMethodsResponse getPaymentMethods(@RequestPayload GetPaymentMethodsRequest request) {
        log.info("SOAP: Solicitando métodos de pago - Request ID: {}", request.getRequestId());
        try {
            GetPaymentMethodsResponse response = new GetPaymentMethodsResponse();
            response.setRequestId(request.getRequestId());
            response.setStatus("SUCCESS");
            
            // Crear lista de métodos de pago
            PaymentMethods paymentMethods = new PaymentMethods();
            paymentMethods.getPaymentMethod().add("TARJETA");
            paymentMethods.getPaymentMethod().add("PAYPAL");
            paymentMethods.getPaymentMethod().add("TRANSFERENCIA");
            paymentMethods.getPaymentMethod().add("EFECTIVO");
            paymentMethods.getPaymentMethod().add("BITCOIN");
            paymentMethods.getPaymentMethod().add("NEQUI");
            
            response.setPaymentMethods(paymentMethods);
            return response;
        } catch (Exception e) {
            log.error("SOAP: Error obteniendo métodos de pago: {}", e.getMessage());
            GetPaymentMethodsResponse response = new GetPaymentMethodsResponse();
            response.setRequestId(request.getRequestId());
            response.setStatus("ERROR");
            return response;
        }
    }
    
    private boolean validatePaymentMethod(String paymentMethod, java.math.BigDecimal amount) {
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) return false;
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) return false;
        String[] validMethods = {"TARJETA", "PAYPAL", "TRANSFERENCIA", "EFECTIVO"};
        for (String validMethod : validMethods) {
            if (validMethod.equalsIgnoreCase(paymentMethod)) return true;
        }
        return false;
    }
}




