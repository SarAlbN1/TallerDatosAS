package cliente.application.services;

import cliente.application.models.Category;
import cliente.application.repositories.CategoryRepository;
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
