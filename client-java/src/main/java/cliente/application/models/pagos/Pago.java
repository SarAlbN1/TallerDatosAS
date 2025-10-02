package cliente.application.models.pagos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "referencia", nullable = false, unique = true, length = 60)
    private String referencia;
    
    @Column(name = "fecha", nullable = false)
    @Builder.Default
    private LocalDateTime fecha = LocalDateTime.now();
    
    @Column(name = "importe", nullable = false, precision = 12, scale = 2)
    private BigDecimal importe;
    
    @Column(name = "moneda", nullable = false, columnDefinition = "CHAR(3)")
    @Builder.Default
    private String moneda = "EUR";
    
    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    @Builder.Default
    private EstadoPago estado = EstadoPago.PENDIENTE;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metodo_id")
    private MetodoPago metodo;
    
    public enum EstadoPago {
        PENDIENTE, CONFIRMADO, FALLIDO, REEMBOLSADO
    }
}