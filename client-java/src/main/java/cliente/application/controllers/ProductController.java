package cliente.application.controllers;

import cliente.application.models.Product;
import cliente.application.services.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

  private final ProductService service;

  public ProductController(ProductService service) {
    this.service = service;
  }

  @GetMapping
  public List<Product> all() {
    return service.findAll();
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Product create(@Valid @RequestBody Product p) {
    return service.create(p);
  }
}
