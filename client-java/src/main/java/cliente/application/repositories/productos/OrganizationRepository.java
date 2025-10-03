package cliente.application.repositories.productos;

import org.springframework.data.jpa.repository.JpaRepository;

import cliente.application.models.productos.Organization;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<Organization, Long> {
  Optional<Organization> findByNameIgnoreCase(String name);
  boolean existsByNameIgnoreCase(String name);
}
