package cliente.application.models.usuarios;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "datos_financieros")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DatosFinancieros {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Usuario usuario;

    @Column(name = "numero_cuenta", length = 34)
    private String numeroCuenta;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_cuenta", length = 20)
    @Builder.Default
    private TipoCuenta tipoCuenta = TipoCuenta.AHORROS;

    @Column(length = 100)
    private String banco;

    @Column(name = "titular_cuenta", length = 200)
    private String titularCuenta;

    @Column(name = "tarjeta_credito", length = 19)
    private String tarjetaCredito;

    @Column(name = "limite_credito", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal limiteCredito = BigDecimal.ZERO;

    @Column(name = "saldo_disponible", precision = 15, scale = 2)
    @Builder.Default
    private BigDecimal saldoDisponible = BigDecimal.ZERO;

    @Column(length = 3)
    @Builder.Default
    private String moneda = "USD";

    @Column(nullable = false)
    @Builder.Default
    private Boolean verificado = false;

    @Column(name = "fecha_verificacion")
    private LocalDateTime fechaVerificacion;

    public enum TipoCuenta {
        AHORROS,
        CORRIENTE,
        NOMINA,
        EMPRESARIAL
    }
}
