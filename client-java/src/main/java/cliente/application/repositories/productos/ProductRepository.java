package cliente.application.repositories.productos;

import cliente.application.models.productos.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // Evita LazyInitializationException: carga organization y category
  @Override
  @EntityGraph(attributePaths = {"organization", "category"})
  @NonNull
  List<Product> findAll();

  @Override
  @EntityGraph(attributePaths = {"organization", "category"})
  @NonNull
  Optional<Product> findById(@NonNull Long id);
}
