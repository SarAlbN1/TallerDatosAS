package cliente.application.repositories.inventario;

import cliente.application.models.inventario.CategoriaInventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoriaInventarioRepository extends JpaRepository<CategoriaInventario, Long> {
    Optional<CategoriaInventario> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
}