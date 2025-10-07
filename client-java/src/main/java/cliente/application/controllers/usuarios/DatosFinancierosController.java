package cliente.application.controllers.usuarios;

import cliente.application.models.usuarios.DatosFinancieros;
import cliente.application.services.usuarios.DatosFinancierosService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/usuarios/datos-financieros")
@CrossOrigin(originPatterns = {"http://localhost:*", "http://127.0.0.1:*"})
public class DatosFinancierosController {

    @Autowired
    private DatosFinancierosService datosFinancierosService;

    @GetMapping
    public ResponseEntity<List<DatosFinancieros>> getAllDatosFinancieros() {
        List<DatosFinancieros> datos = datosFinancierosService.getAllDatosFinancieros();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosFinancieros> getDatosFinancierosById(@PathVariable Long id) {
        return datosFinancierosService.getDatosFinancierosById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DatosFinancieros> getDatosFinancierosByUsuarioId(@PathVariable Long usuarioId) {
        return datosFinancierosService.getDatosFinancierosByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/verificados")
    public ResponseEntity<List<DatosFinancieros>> getDatosFinancierosVerificados() {
        List<DatosFinancieros> datos = datosFinancierosService.getDatosFinancierosVerificados();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/no-verificados")
    public ResponseEntity<List<DatosFinancieros>> getDatosFinancierosNoVerificados() {
        List<DatosFinancieros> datos = datosFinancierosService.getDatosFinancierosNoVerificados();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/banco/{banco}")
    public ResponseEntity<List<DatosFinancieros>> getDatosFinancierosByBanco(@PathVariable String banco) {
        List<DatosFinancieros> datos = datosFinancierosService.getDatosFinancierosByBanco(banco);
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/moneda/{moneda}")
    public ResponseEntity<List<DatosFinancieros>> getDatosFinancierosByMoneda(@PathVariable String moneda) {
        List<DatosFinancieros> datos = datosFinancierosService.getDatosFinancierosByMoneda(moneda);
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/tipo-cuenta/{tipoCuenta}")
    public ResponseEntity<List<DatosFinancieros>> getDatosFinancierosByTipoCuenta(@PathVariable DatosFinancieros.TipoCuenta tipoCuenta) {
        List<DatosFinancieros> datos = datosFinancierosService.getDatosFinancierosByTipoCuenta(tipoCuenta);
        return ResponseEntity.ok(datos);
    }

    @PostMapping
    public ResponseEntity<DatosFinancieros> createDatosFinancieros(@RequestBody DatosFinancieros datosFinancieros) {
        DatosFinancieros savedDatos = datosFinancierosService.saveDatosFinancieros(datosFinancieros);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDatos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatosFinancieros> updateDatosFinancieros(@PathVariable Long id, @RequestBody DatosFinancieros datosFinancieros) {
        try {
            DatosFinancieros updatedDatos = datosFinancierosService.updateDatosFinancieros(id, datosFinancieros);
            return ResponseEntity.ok(updatedDatos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDatosFinancieros(@PathVariable Long id) {
        if (!datosFinancierosService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datosFinancierosService.deleteDatosFinancieros(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/verificar")
    public ResponseEntity<DatosFinancieros> verificarDatosFinancieros(@PathVariable Long id) {
        try {
            DatosFinancieros datos = datosFinancierosService.verificarDatosFinancieros(id);
            return ResponseEntity.ok(datos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/saldo")
    public ResponseEntity<DatosFinancieros> actualizarSaldo(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        try {
            BigDecimal nuevoSaldo = body.get("saldo");
            if (nuevoSaldo == null || nuevoSaldo.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().build();
            }
            DatosFinancieros datos = datosFinancierosService.actualizarSaldo(id, nuevoSaldo);
            return ResponseEntity.ok(datos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/limite-credito")
    public ResponseEntity<DatosFinancieros> actualizarLimiteCredito(@PathVariable Long id, @RequestBody Map<String, BigDecimal> body) {
        try {
            BigDecimal nuevoLimite = body.get("limiteCredito");
            if (nuevoLimite == null || nuevoLimite.compareTo(BigDecimal.ZERO) < 0) {
                return ResponseEntity.badRequest().build();
            }
            DatosFinancieros datos = datosFinancierosService.actualizarLimiteCredito(id, nuevoLimite);
            return ResponseEntity.ok(datos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
