package cliente.application.controllers;

import cliente.application.dto.UserResponse;
import cliente.application.services.UserServiceImpl;
import cliente.soap.users.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

/**
 * Endpoint SOAP para servicios de usuario
 * 
 * WSDL disponible en: http://localhost:8080/ws/users.wsdl
 * 
 * Ejemplo de uso con SoapUI:
 * 1. Crear nuevo proyecto SOAP
 * 2. Importar WSDL desde: http://localhost:8080/ws/users.wsdl
 * 3. Ejecutar operación GetRandomUser
 */
@Endpoint
@RequiredArgsConstructor
@Slf4j
public class UserServiceEndpoint {
    
    private static final String NAMESPACE_URI = "http://cliente.com/users";
    
    private final UserServiceImpl userService;
    
    /**
     * Obtiene un usuario aleatorio
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "GetRandomUserRequest")
    @ResponsePayload
    public GetRandomUserResponse getRandomUser(@RequestPayload GetRandomUserRequest request) {
        log.info("SOAP: Solicitando usuario aleatorio - Request ID: {}", request.getRequestId());
        
        try {
            UserResponse userDto = userService.getRandomUser();
            
            // Mapear DTO a objeto SOAP
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
            
            log.info("SOAP: Usuario aleatorio obtenido - ID: {} - Nombre: {}", 
                    user.getId(), user.getNombre());
            
            return response;
            
        } catch (Exception e) {
            log.error("SOAP: Error obteniendo usuario aleatorio: {}", e.getMessage());
            
            GetRandomUserResponse response = new GetRandomUserResponse();
            response.setRequestId(request.getRequestId());
            response.setStatus("ERROR");
            
            return response;
        }
    }
    
    /**
     * Valida un método de pago
     */
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "ValidatePaymentRequest")
    @ResponsePayload
    public ValidatePaymentResponse validatePayment(@RequestPayload ValidatePaymentRequest request) {
        log.info("SOAP: Validando pago - Método: {} - Monto: {} - Request ID: {}", 
                request.getPaymentMethod(), request.getAmount(), request.getRequestId());
        
        try {
            // Lógica de validación simple
            boolean isValid = validatePaymentMethod(request.getPaymentMethod(), request.getAmount());
            
            ValidatePaymentResponse response = new ValidatePaymentResponse();
            response.setIsValid(isValid);
            response.setRequestId(request.getRequestId());
            response.setStatus("SUCCESS");
            response.setMessage(isValid ? "Pago válido" : "Pago inválido");
            
            log.info("SOAP: Validación de pago completada - Válido: {}", isValid);
            
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
    
    /**
     * Valida un método de pago (lógica de negocio)
     */
    private boolean validatePaymentMethod(String paymentMethod, java.math.BigDecimal amount) {
        // Validaciones simples
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            return false;
        }
        
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            return false;
        }
        
        // Métodos de pago válidos
        String[] validMethods = {"TARJETA", "PAYPAL", "TRANSFERENCIA", "EFECTIVO"};
        for (String validMethod : validMethods) {
            if (validMethod.equalsIgnoreCase(paymentMethod)) {
                return true;
            }
        }
        
        return false;
    }
}
