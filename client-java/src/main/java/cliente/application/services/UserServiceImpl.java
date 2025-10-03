package cliente.application.services;

import cliente.application.ports.in.UserService;
import cliente.application.repositories.usuarios.UsuarioRepository;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

  private final UsuarioRepository usuarios;

  public UserServiceImpl(UsuarioRepository usuarios) {
    this.usuarios = usuarios;
  }

  @Override
  public String resolveUserReference(Long userId) {
    return usuarios.findById(userId)
        .map(u -> "CUST-" + u.getId())
        .orElse(null);
  }
}
