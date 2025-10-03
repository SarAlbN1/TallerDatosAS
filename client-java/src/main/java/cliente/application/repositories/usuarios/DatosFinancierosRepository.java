package cliente.application.repositories.usuarios;

import cliente.application.models.usuarios.DatosFinancieros;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatosFinancierosRepository extends JpaRepository<DatosFinancieros, Long> {
    Optional<DatosFinancieros> findByUsuarioId(Long usuarioId);
    List<DatosFinancieros> findByVerificado(Boolean verificado);
    List<DatosFinancieros> findByBanco(String banco);
    List<DatosFinancieros> findByMoneda(String moneda);
    List<DatosFinancieros> findByTipoCuenta(DatosFinancieros.TipoCuenta tipoCuenta);
}
