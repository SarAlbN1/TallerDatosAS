package cliente.application.services.productos;

import cliente.application.models.productos.Category;
import cliente.application.repositories.productos.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {

  private final CategoryRepository repo;

  public CategoryService(CategoryRepository repo) {
    this.repo = repo;
  }

  public List<Category> findAll() {
    return repo.findAll();
  }

  public Optional<Category> findById(Long id) {
    return repo.findById(id);
  }

  @Transactional
  public Category create(Category c) {
    return repo.save(c);
  }
}
