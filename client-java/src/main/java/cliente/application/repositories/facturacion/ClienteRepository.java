package cliente.application.repositories.facturacion;

import cliente.application.models.facturacion.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByNif(String nif);
    boolean existsByEmail(String email);
    boolean existsByNif(String nif);
}