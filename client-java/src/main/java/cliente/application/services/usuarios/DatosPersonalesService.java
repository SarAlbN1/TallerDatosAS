package cliente.application.services.usuarios;

import cliente.application.models.usuarios.DatosPersonales;
import cliente.application.repositories.usuarios.DatosPersonalesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional(transactionManager = "usuariosTransactionManager")
public class DatosPersonalesService {

    @Autowired
    private DatosPersonalesRepository datosPersonalesRepository;

    public List<DatosPersonales> getAllDatosPersonales() {
        return datosPersonalesRepository.findAll();
    }

    public Optional<DatosPersonales> getDatosPersonalesById(Long id) {
        return datosPersonalesRepository.findById(id);
    }

    public Optional<DatosPersonales> getDatosPersonalesByUsuarioId(Long usuarioId) {
        return datosPersonalesRepository.findByUsuarioId(usuarioId);
    }

    public Optional<DatosPersonales> getDatosPersonalesByDocumento(String documentoIdentidad) {
        return datosPersonalesRepository.findByDocumentoIdentidad(documentoIdentidad);
    }

    public List<DatosPersonales> searchByNombreOrApellido(String texto) {
        return datosPersonalesRepository.findByNombreContainingIgnoreCaseOrApellidoContainingIgnoreCase(texto, texto);
    }

    public List<DatosPersonales> getDatosPersonalesByPais(String pais) {
        return datosPersonalesRepository.findByPais(pais);
    }

    public List<DatosPersonales> getDatosPersonalesByCiudad(String ciudad) {
        return datosPersonalesRepository.findByCiudad(ciudad);
    }

    public DatosPersonales saveDatosPersonales(DatosPersonales datosPersonales) {
        return datosPersonalesRepository.save(datosPersonales);
    }

    public DatosPersonales updateDatosPersonales(Long id, DatosPersonales datosPersonales) {
        if (!datosPersonalesRepository.existsById(id)) {
            throw new IllegalArgumentException("Datos personales con ID " + id + " no existen");
        }
        datosPersonales.setId(id);
        return datosPersonalesRepository.save(datosPersonales);
    }

    public void deleteDatosPersonales(Long id) {
        datosPersonalesRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return datosPersonalesRepository.existsById(id);
    }

    public boolean existsByDocumento(String documentoIdentidad) {
        return datosPersonalesRepository.existsByDocumentoIdentidad(documentoIdentidad);
    }
}
