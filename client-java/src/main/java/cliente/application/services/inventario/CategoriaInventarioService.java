package cliente.application.services.inventario;

import cliente.application.models.inventario.CategoriaInventario;
import cliente.application.repositories.inventario.CategoriaInventarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoriaInventarioService {

    private final CategoriaInventarioRepository repository;

    public CategoriaInventarioService(CategoriaInventarioRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
    public List<CategoriaInventario> findAll() {
        return repository.findAll();
    }

    @Transactional(readOnly = true, transactionManager = "jtaTransactionManager")
    public Optional<CategoriaInventario> findById(Long id) {
        return repository.findById(id);
    }

    @Transactional(transactionManager = "jtaTransactionManager")
    public CategoriaInventario save(CategoriaInventario categoria) {
        return repository.save(categoria);
    }
}
