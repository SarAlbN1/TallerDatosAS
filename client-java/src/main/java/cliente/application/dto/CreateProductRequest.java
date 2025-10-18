package cliente.application.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * DTO para crear productos
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateProductRequest {
    
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;
    
    @NotBlank(message = "El SKU es obligatorio")
    private String sku;
    
    @NotNull(message = "El stock es obligatorio")
    @Positive(message = "El stock debe ser positivo")
    private Integer stock;
    
    @NotNull(message = "La categoría es obligatoria")
    private Long categoriaId;
    
    private String descripcion;
}
