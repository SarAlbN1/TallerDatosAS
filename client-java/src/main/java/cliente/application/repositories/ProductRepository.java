package cliente.application.repositories;

import cliente.application.models.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

  // Trae organization y category inicializados para evitar LazyInitializationException
  @Override
  @EntityGraph(attributePaths = {"organization", "category"})
  List<Product> findAll();

  @Override
  @EntityGraph(attributePaths = {"organization", "category"})
  Optional<Product> findById(Long id);
}
