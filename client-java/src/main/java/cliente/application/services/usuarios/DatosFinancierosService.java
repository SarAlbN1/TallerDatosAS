package cliente.application.services.usuarios;

import cliente.application.models.usuarios.DatosFinancieros;
import cliente.application.repositories.usuarios.DatosFinancierosRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(transactionManager = "usuariosTransactionManager")
public class DatosFinancierosService {

    @Autowired
    private DatosFinancierosRepository datosFinancierosRepository;

    public List<DatosFinancieros> getAllDatosFinancieros() {
        return datosFinancierosRepository.findAll();
    }

    public Optional<DatosFinancieros> getDatosFinancierosById(Long id) {
        return datosFinancierosRepository.findById(id);
    }

    public Optional<DatosFinancieros> getDatosFinancierosByUsuarioId(Long usuarioId) {
        return datosFinancierosRepository.findByUsuarioId(usuarioId);
    }

    public List<DatosFinancieros> getDatosFinancierosVerificados() {
        return datosFinancierosRepository.findByVerificado(true);
    }

    public List<DatosFinancieros> getDatosFinancierosNoVerificados() {
        return datosFinancierosRepository.findByVerificado(false);
    }

    public List<DatosFinancieros> getDatosFinancierosByBanco(String banco) {
        return datosFinancierosRepository.findByBanco(banco);
    }

    public List<DatosFinancieros> getDatosFinancierosByMoneda(String moneda) {
        return datosFinancierosRepository.findByMoneda(moneda);
    }

    public List<DatosFinancieros> getDatosFinancierosByTipoCuenta(DatosFinancieros.TipoCuenta tipoCuenta) {
        return datosFinancierosRepository.findByTipoCuenta(tipoCuenta);
    }

    public DatosFinancieros saveDatosFinancieros(DatosFinancieros datosFinancieros) {
        return datosFinancierosRepository.save(datosFinancieros);
    }

    public DatosFinancieros updateDatosFinancieros(Long id, DatosFinancieros datosFinancieros) {
        if (!datosFinancierosRepository.existsById(id)) {
            throw new IllegalArgumentException("Datos financieros con ID " + id + " no existen");
        }
        datosFinancieros.setId(id);
        return datosFinancierosRepository.save(datosFinancieros);
    }

    public void deleteDatosFinancieros(Long id) {
        datosFinancierosRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return datosFinancierosRepository.existsById(id);
    }

    public DatosFinancieros verificarDatosFinancieros(Long id) {
        DatosFinancieros datos = datosFinancierosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Datos financieros con ID " + id + " no existen"));
        datos.setVerificado(true);
        datos.setFechaVerificacion(LocalDateTime.now());
        return datosFinancierosRepository.save(datos);
    }

    public DatosFinancieros actualizarSaldo(Long id, BigDecimal nuevoSaldo) {
        DatosFinancieros datos = datosFinancierosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Datos financieros con ID " + id + " no existen"));
        datos.setSaldoDisponible(nuevoSaldo);
        return datosFinancierosRepository.save(datos);
    }

    public DatosFinancieros actualizarLimiteCredito(Long id, BigDecimal nuevoLimite) {
        DatosFinancieros datos = datosFinancierosRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Datos financieros con ID " + id + " no existen"));
        datos.setLimiteCredito(nuevoLimite);
        return datosFinancierosRepository.save(datos);
    }
}
