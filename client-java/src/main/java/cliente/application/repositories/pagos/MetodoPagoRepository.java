package cliente.application.repositories.pagos;

import cliente.application.models.pagos.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MetodoPagoRepository extends JpaRepository<MetodoPago, Long> {
    Optional<MetodoPago> findByCodigo(String codigo);
    boolean existsByCodigo(String codigo);
}