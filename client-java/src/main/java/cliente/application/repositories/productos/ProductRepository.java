package cliente.application.repositories.productos;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import cliente.application.models.productos.Product;

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
