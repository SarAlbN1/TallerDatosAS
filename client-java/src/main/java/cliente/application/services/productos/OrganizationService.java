package cliente.application.services.productos;

import cliente.application.models.productos.Organization;
import cliente.application.repositories.productos.OrganizationRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class OrganizationService {

  private final OrganizationRepository repo;

  public OrganizationService(OrganizationRepository repo) {
    this.repo = repo;
  }

  public List<Organization> findAll() {
    return repo.findAll();
  }

  @Transactional
  public Organization create(Organization o) {
    return repo.save(o);
  }
}
