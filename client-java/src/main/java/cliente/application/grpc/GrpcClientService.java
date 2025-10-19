package cliente.application.grpc;

import cliente.application.dto.CheckoutRequest;
import cliente.application.dto.CheckoutResponse;
import cliente.application.services.CheckoutCoordinatorService;
import cliente.application.services.PurchaseServiceImpl;
import cliente.application.services.usuarios.UsuarioService;
import cliente.application.models.usuarios.Usuario;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Servicio que actúa como cliente para los servicios gRPC locales
 * 
 * Este servicio usa directamente los servicios locales en lugar de
 * conectarse a servicios gRPC externos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class GrpcClientService {
    
    private final PurchaseServiceImpl purchaseService;
    private final UsuarioService userService;
    private final CheckoutCoordinatorService checkoutCoordinatorService;
    
    /**
     * Obtiene un usuario aleatorio usando el servicio local
     */
    public cliente.application.dto.UserResponse getRandomUser() {
        log.info("Local Service: Solicitando usuario aleatorio");
        
        try {
            List<Usuario> allUsers = userService.getAllUsuarios();
            if (allUsers.isEmpty()) {
                throw new RuntimeException("No hay usuarios disponibles");
            }
            
            Usuario randomUser = allUsers.get((int)(System.currentTimeMillis() % allUsers.size()));
            var dp = randomUser.getDatosPersonales();
            String nombreCompleto = dp != null ? 
                (dp.getNombre() + (dp.getApellido() != null ? (" " + dp.getApellido()) : "")) : 
                randomUser.getUsername();
            
            cliente.application.dto.UserResponse userDto = cliente.application.dto.UserResponse.builder()
                .id(randomUser.getId())
                .nombre(nombreCompleto)
                .email(randomUser.getEmail())
                .telefono(dp != null ? dp.getTelefono() : "")
                .direccion(dp != null ? dp.getDireccion() : "")
                .ciudad(dp != null ? dp.getCiudad() : "")
                .pais(dp != null ? dp.getPais() : "")
                .build();
            
            log.info("Local Service: Usuario obtenido - ID: {} - Nombre: {}", 
                    randomUser.getId(), nombreCompleto);
            
            return userDto;
            
        } catch (Exception e) {
            log.error("Local Service: Error obteniendo usuario: {}", e.getMessage());
            throw new RuntimeException("Error obteniendo usuario: " + e.getMessage());
        }
    }
    
    /**
     * Procesa una compra usando el CheckoutCoordinatorService (con Kafka)
     */
    public CheckoutResponse processPurchase(CheckoutRequest request) {
        log.info("Local Service: Procesando compra - Cliente: {} - Items: {}", 
                request.getClienteId(), request.getItems().size());
        
        try {
            // Procesar cada item usando CheckoutCoordinatorService
            List<CheckoutResponse.CheckoutItemResponse> itemsResponse = new ArrayList<>();
            double total = 0.0;
            
            for (CheckoutRequest.CheckoutItem item : request.getItems()) {
                // Usar CheckoutCoordinatorService para procesar cada item
                String resultado = checkoutCoordinatorService.procesarVentaDemo(
                    item.getSku(), 
                    item.getCantidad(), 
                    request.getClienteId(), 
                    request.getMetodoPago() != null ? request.getMetodoPago() : "TARJETA"
                );
                
                double precioUnitario = 100.0; // Precio ejemplo
                double subtotal = precioUnitario * item.getCantidad();
                total += subtotal;
                
                itemsResponse.add(CheckoutResponse.CheckoutItemResponse.builder()
                    .sku(item.getSku())
                    .nombre("Producto " + item.getSku())
                    .cantidad(item.getCantidad())
                    .precioUnitario(java.math.BigDecimal.valueOf(precioUnitario))
                    .subtotal(java.math.BigDecimal.valueOf(subtotal))
                    .build());
            }
            
            String orderId = "ORDER-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            String txId = "TX-" + System.currentTimeMillis();
            
            CheckoutResponse response = CheckoutResponse.builder()
                .orderId(orderId)
                .status("COMPLETED")
                .txId(txId)
                .clienteId(request.getClienteId())
                .total(java.math.BigDecimal.valueOf(total))
                .numeroFactura("FACT-" + System.currentTimeMillis())
                .referenciaPago("PAG-" + System.currentTimeMillis())
                .fechaProcesamiento(LocalDateTime.now())
                .items(itemsResponse)
                .build();
            
            log.info("Local Service: Compra procesada - Order ID: {} - Status: {} - Total: {}", 
                    response.getOrderId(), response.getStatus(), response.getTotal());
            
            return response;
            
        } catch (Exception e) {
            log.error("Local Service: Error procesando compra: {}", e.getMessage());
            
            return CheckoutResponse.builder()
                .orderId("ERROR-" + System.currentTimeMillis())
                .status("FAILED")
                .txId("TX-ERROR-" + System.currentTimeMillis())
                .clienteId(request.getClienteId())
                .total(java.math.BigDecimal.ZERO)
                .fechaProcesamiento(LocalDateTime.now())
                .items(new ArrayList<>())
                .build();
        }
    }
    
    /**
     * Valida stock usando el servicio local
     */
    public boolean validateStock(String sku, Integer cantidad) {
        log.info("Local Service: Validando stock - SKU: {} - Cantidad: {}", sku, cantidad);
        
        try {
            // Validación simple de stock
            boolean disponible = cantidad <= 100;
            
            log.info("Local Service: Validación de stock - SKU: {} - Disponible: {}", 
                    sku, disponible);
            
            return disponible;
            
        } catch (Exception e) {
            log.error("Local Service: Error validando stock: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Confirma una orden usando el servicio local
     */
    public String confirmOrder(String orderId, String txId, Long clienteId) {
        log.info("Local Service: Confirmando orden - Order ID: {} - TX ID: {}", orderId, txId);
        
        try {
            String confirmationCode = "CONF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            
            log.info("Local Service: Orden confirmada - Order ID: {} - Confirmation Code: {}", 
                    orderId, confirmationCode);
            
            return confirmationCode;
            
        } catch (Exception e) {
            log.error("Local Service: Error confirmando orden: {}", e.getMessage());
            return "ERROR";
        }
    }
}
