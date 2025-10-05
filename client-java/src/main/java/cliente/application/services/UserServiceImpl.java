package cliente.application.services;

import cliente.application.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Implementación del servicio de usuarios (SOAP)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl {
    
    private final Random random = new Random();
    
    // Datos de usuarios simulados
    private final List<UserResponse> usuariosSimulados = Arrays.asList(
        UserResponse.builder()
            .id(1L)
            .nombre("Juan Pérez")
            .email("juan.perez@email.com")
            .telefono("+34 600 123 456")
            .direccion("Calle Mayor 123")
            .ciudad("Madrid")
            .pais("España")
            .build(),
        UserResponse.builder()
            .id(2L)
            .nombre("María García")
            .email("maria.garcia@email.com")
            .telefono("+34 600 234 567")
            .direccion("Avenida de la Paz 45")
            .ciudad("Barcelona")
            .pais("España")
            .build(),
        UserResponse.builder()
            .id(3L)
            .nombre("Carlos López")
            .email("carlos.lopez@email.com")
            .telefono("+34 600 345 678")
            .direccion("Plaza España 78")
            .ciudad("Valencia")
            .pais("España")
            .build(),
        UserResponse.builder()
            .id(4L)
            .nombre("Ana Martínez")
            .email("ana.martinez@email.com")
            .telefono("+34 600 456 789")
            .direccion("Calle del Sol 12")
            .ciudad("Sevilla")
            .pais("España")
            .build(),
        UserResponse.builder()
            .id(5L)
            .nombre("Pedro Rodríguez")
            .email("pedro.rodriguez@email.com")
            .telefono("+34 600 567 890")
            .direccion("Gran Vía 234")
            .ciudad("Bilbao")
            .pais("España")
            .build()
    );
    
    /**
     * Obtiene un usuario aleatorio
     */
    public UserResponse getRandomUser() {
        log.info("Obteniendo usuario aleatorio");
        
        int index = random.nextInt(usuariosSimulados.size());
        UserResponse usuario = usuariosSimulados.get(index);
        
        log.info("Usuario seleccionado: {} - ID: {}", usuario.getNombre(), usuario.getId());
        return usuario;
    }
    
    /**
     * Obtiene un usuario por ID
     */
    public UserResponse getUserById(Long id) {
        log.info("Obteniendo usuario con ID: {}", id);
        
        return usuariosSimulados.stream()
            .filter(user -> user.getId().equals(id))
            .findFirst()
            .orElse(null);
    }
    
    /**
     * Obtiene todos los usuarios
     */
    public List<UserResponse> getAllUsers() {
        log.info("Obteniendo todos los usuarios");
        return usuariosSimulados;
    }
}