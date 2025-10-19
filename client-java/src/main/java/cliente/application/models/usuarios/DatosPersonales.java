package cliente.application.models.usuarios;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "datos_personales")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class DatosPersonales {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonIgnoreProperties({"datosPersonales", "datosFinancieros"})
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String apellido;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(length = 100)
    private String ciudad;

    @Column(length = 100)
    private String pais;

    @Column(name = "codigo_postal", length = 20)
    private String codigoPostal;

    @Column(name = "documento_identidad", unique = true, length = 50)
    private String documentoIdentidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20)
    @Builder.Default
    private TipoDocumento tipoDocumento = TipoDocumento.DNI;

    public enum TipoDocumento {
        DNI,
        PASAPORTE,
        CEDULA,
        OTRO
    }
}
