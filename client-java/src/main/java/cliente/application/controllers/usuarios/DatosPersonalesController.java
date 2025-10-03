package cliente.application.controllers.usuarios;

import cliente.application.models.usuarios.DatosPersonales;
import cliente.application.services.usuarios.DatosPersonalesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/datos-personales")
@CrossOrigin(origins = "*")
public class DatosPersonalesController {

    @Autowired
    private DatosPersonalesService datosPersonalesService;

    @GetMapping
    public ResponseEntity<List<DatosPersonales>> getAllDatosPersonales() {
        List<DatosPersonales> datos = datosPersonalesService.getAllDatosPersonales();
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DatosPersonales> getDatosPersonalesById(@PathVariable Long id) {
        return datosPersonalesService.getDatosPersonalesById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<DatosPersonales> getDatosPersonalesByUsuarioId(@PathVariable Long usuarioId) {
        return datosPersonalesService.getDatosPersonalesByUsuarioId(usuarioId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/documento/{documentoIdentidad}")
    public ResponseEntity<DatosPersonales> getDatosPersonalesByDocumento(@PathVariable String documentoIdentidad) {
        return datosPersonalesService.getDatosPersonalesByDocumento(documentoIdentidad)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DatosPersonales>> searchByNombreOrApellido(@RequestParam String texto) {
        List<DatosPersonales> datos = datosPersonalesService.searchByNombreOrApellido(texto);
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/pais/{pais}")
    public ResponseEntity<List<DatosPersonales>> getDatosPersonalesByPais(@PathVariable String pais) {
        List<DatosPersonales> datos = datosPersonalesService.getDatosPersonalesByPais(pais);
        return ResponseEntity.ok(datos);
    }

    @GetMapping("/ciudad/{ciudad}")
    public ResponseEntity<List<DatosPersonales>> getDatosPersonalesByCiudad(@PathVariable String ciudad) {
        List<DatosPersonales> datos = datosPersonalesService.getDatosPersonalesByCiudad(ciudad);
        return ResponseEntity.ok(datos);
    }

    @PostMapping
    public ResponseEntity<DatosPersonales> createDatosPersonales(@RequestBody DatosPersonales datosPersonales) {
        if (datosPersonales.getDocumentoIdentidad() != null 
            && datosPersonalesService.existsByDocumento(datosPersonales.getDocumentoIdentidad())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        DatosPersonales savedDatos = datosPersonalesService.saveDatosPersonales(datosPersonales);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDatos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DatosPersonales> updateDatosPersonales(@PathVariable Long id, @RequestBody DatosPersonales datosPersonales) {
        try {
            DatosPersonales updatedDatos = datosPersonalesService.updateDatosPersonales(id, datosPersonales);
            return ResponseEntity.ok(updatedDatos);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDatosPersonales(@PathVariable Long id) {
        if (!datosPersonalesService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        datosPersonalesService.deleteDatosPersonales(id);
        return ResponseEntity.noContent().build();
    }
}
