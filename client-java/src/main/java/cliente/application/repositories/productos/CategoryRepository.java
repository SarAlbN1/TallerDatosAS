package cliente.application.repositories.productos;

import org.springframework.data.jpa.repository.JpaRepository;

import cliente.application.models.productos.Category;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
  Optional<Category> findByNameIgnoreCase(String name);
  boolean existsByNameIgnoreCase(String name);
}
