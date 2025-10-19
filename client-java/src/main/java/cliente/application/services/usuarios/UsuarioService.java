package cliente.application.services.usuarios;

import cliente.application.models.usuarios.Usuario;
import cliente.application.repositories.usuarios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(transactionManager = "usuariosTransactionManager")
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    public List<Usuario> getAllUsuarios() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> getUsuarioById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Optional<Usuario> getUsuarioByUsername(String username) {
        return usuarioRepository.findByUsername(username);
    }

    public Optional<Usuario> getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email);
    }

    public List<Usuario> getUsuariosActivos() {
        return usuarioRepository.findByActivo(true);
    }

    public List<Usuario> getUsuariosInactivos() {
        return usuarioRepository.findByActivo(false);
    }

    public Usuario saveUsuario(Usuario usuario) {
        return usuarioRepository.save(usuario);
    }

    public Usuario createUsuario(Usuario usuario) {
        if (usuario.getFechaRegistro() == null) {
            usuario.setFechaRegistro(LocalDateTime.now());
        }
        return usuarioRepository.save(usuario);
    }

    public Usuario updateUsuario(Long id, Usuario usuario) {
        if (!usuarioRepository.existsById(id)) {
            throw new IllegalArgumentException("Usuario con ID " + id + " no existe");
        }
        usuario.setId(id);
        return usuarioRepository.save(usuario);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return usuarioRepository.existsById(id);
    }

    public boolean existsByUsername(String username) {
        return usuarioRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return usuarioRepository.existsByEmail(email);
    }

    public Usuario activarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + id + " no existe"));
        usuario.setActivo(true);
        return usuarioRepository.save(usuario);
    }

    public Usuario desactivarUsuario(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + id + " no existe"));
        usuario.setActivo(false);
        return usuarioRepository.save(usuario);
    }

    public Usuario registrarSesion(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Usuario con ID " + id + " no existe"));
        usuario.setUltimaSesion(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }
}
