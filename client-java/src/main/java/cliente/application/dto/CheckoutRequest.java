package cliente.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para solicitud de checkout
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckoutRequest {
    
    @NotEmpty(message = "Items son requeridos")
    @Valid
    private List<CheckoutItem> items;
    
    // FIX: permitir null para que el backend resuelva usuario aleatorio si no se envía
    private Long clienteId;
    
    @NotNull(message = "Método de pago es requerido")
    private String metodoPago;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CheckoutItem {
        
        @NotNull(message = "SKU es requerido")
        private String sku;
        
        @NotNull(message = "Cantidad es requerida")
        private Integer cantidad;
    }
}
