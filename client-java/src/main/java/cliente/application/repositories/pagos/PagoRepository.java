package cliente.application.repositories.pagos;

import cliente.application.models.pagos.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {
    Optional<Pago> findByReferencia(String referencia);
    List<Pago> findByEstado(Pago.EstadoPago estado);
    List<Pago> findByMetodoId(Long metodoId);
    boolean existsByReferencia(String referencia);
}