package cliente.application.services;

import cliente.application.models.Product;
import cliente.application.repositories.ProductRepository;
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

  @Transactional
  public Product create(Product p) {
    return repo.save(p);
  }
}
