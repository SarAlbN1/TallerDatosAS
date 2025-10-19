package cliente.application.repositories.usuarios;

import cliente.application.models.usuarios.DatosPersonales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DatosPersonalesRepository extends JpaRepository<DatosPersonales, Long> {
    Optional<DatosPersonales> findByUsuarioId(Long usuarioId);
    Optional<DatosPersonales> findByDocumentoIdentidad(String documentoIdentidad);
    List<DatosPersonales> findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(String nombre, String apellido);
    List<DatosPersonales> findByPais(String pais);
    List<DatosPersonales> findByCiudad(String ciudad);
    boolean existsByDocumentoIdentidad(String documentoIdentidad);
}
