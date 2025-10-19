package cliente.application.models.inventario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "items")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "sku", nullable = false, unique = true, length = 64)
    private String sku;
    
    @Column(name = "nombre", nullable = false, length = 160)
    private String nombre;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "categoria_id")
    private CategoriaInventario categoria;
    
    /**
     * ID del producto asociado en la base de datos 'productos'.
     * NOTA: No se usa @ManyToOne porque las entidades están en diferentes datasources.
     * La integridad referencial se maneja a nivel de aplicación.
     */
    @Column(name = "producto_id")
    private Long productoId;
    
    @Column(name = "stock", nullable = false)
    @Builder.Default
    private Integer stock = 0;
}