package cliente.application.services.productos;

import cliente.application.models.productos.Product;
import cliente.application.repositories.productos.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductService {

  private final ProductRepository repo;

  public ProductService(ProductRepository repo) {
    this.repo = repo;
  }

  @Transactional(readOnly = true)
  public List<Product> findAll() {
    // gracias al @EntityGraph en el repo, organization y category vienen cargados
    return repo.findAll();
  }

  @Transactional(readOnly = true)
  public java.util.Optional<Product> findById(Long id) {
    return repo.findById(id);
  }

  @Transactional
  public Product create(Product p) {
    return repo.save(p);
  }
}
