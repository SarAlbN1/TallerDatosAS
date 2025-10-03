package cliente.application.services.productos;

import cliente.application.models.productos.Category;
import cliente.application.repositories.productos.CategoryRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

  private final CategoryRepository repo;

  public CategoryService(CategoryRepository repo) {
    this.repo = repo;
  }

  public List<Category> findAll() {
    return repo.findAll();
  }

  @Transactional
  public Category create(Category c) {
    return repo.save(c);
  }
}
