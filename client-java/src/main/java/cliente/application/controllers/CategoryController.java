package cliente.application.controllers;

import cliente.application.models.Category;
import cliente.application.services.CategoryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

  private final CategoryService service;

  public CategoryController(CategoryService service) {
    this.service = service;
  }

  @GetMapping
  public List<Category> all() {
    return service.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Category create(@Valid @RequestBody Category c) {
    return service.create(c);
  }
}
