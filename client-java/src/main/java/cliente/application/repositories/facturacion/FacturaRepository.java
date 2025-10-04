package cliente.application.repositories.facturacion;

import cliente.application.models.facturacion.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface FacturaRepository extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumero(String numero);
    List<Factura> findByClienteId(Long clienteId);
    List<Factura> findByFechaBetween(LocalDate fechaInicio, LocalDate fechaFin);
    boolean existsByNumero(String numero);
}