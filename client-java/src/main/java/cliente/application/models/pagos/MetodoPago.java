package cliente.application.models.pagos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "metodos_pago")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MetodoPago {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "codigo", nullable = false, unique = true, length = 40)
    private String codigo;
    
    @Column(name = "descripcion", length = 160)
    private String descripcion;
    
    @OneToMany(mappedBy = "metodo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Pago> pagos;
}