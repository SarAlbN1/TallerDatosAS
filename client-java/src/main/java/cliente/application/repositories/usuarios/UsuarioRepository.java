package cliente.application.repositories.usuarios;

import cliente.application.models.usuarios.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
    Optional<Usuario> findByEmail(String email);
    List<Usuario> findByActivo(Boolean activo);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
