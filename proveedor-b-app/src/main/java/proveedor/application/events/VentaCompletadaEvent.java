package proveedor.application.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VentaCompletadaEvent {
    
    private String facturaId;
    private Long clienteId;
    private String clienteEmail;
    private List<ProductoVenta> productos;
    private BigDecimal total;
    private LocalDateTime fecha;
    private String metodoPago;
    private String referenciaPago;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ProductoVenta {
        private String sku;
        private String nombre;
        private Integer cantidad;
        private BigDecimal precio;
        private String proveedor;
    }
}