package cliente.application.repositories.inventario;

import cliente.application.models.inventario.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findBySku(String sku);
    List<Item> findByNombreContainingIgnoreCase(String nombre);
    List<Item> findByCategoriaId(Long categoriaId);
    List<Item> findByStockLessThan(Integer stockMinimo);
    boolean existsBySku(String sku);
}