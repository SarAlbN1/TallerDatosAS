package cliente.application.grpc;

import cliente.application.services.UserServiceImpl;
import cliente.grpc.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio gRPC de usuarios
 * 
 * Este servicio maneja las operaciones de usuario a través de gRPC
 * y usa UserServiceImpl como backend
 */
@GrpcService
@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    
    private final UserServiceImpl userService;
    
    /**
     * Obtiene un usuario aleatorio
     */
    @Override
    public void getRandomUser(GetRandomUserRequest request, StreamObserver<GetRandomUserResponse> responseObserver) {
        log.info("gRPC: Solicitando usuario aleatorio - Request ID: {}", request.getRequestId());
        
        try {
            // Obtener usuario aleatorio del servicio
            var userDto = userService.getRandomUser();
            
            // Mapear DTO a objeto gRPC
            User user = User.newBuilder()
                .setId(userDto.getId())
                .setNombre(userDto.getNombre())
                .setEmail(userDto.getEmail())
                .setTelefono(userDto.getTelefono())
                .setDireccion(userDto.getDireccion())
                .setCiudad(userDto.getCiudad())
                .setPais(userDto.getPais())
                .build();
            
            GetRandomUserResponse response = GetRandomUserResponse.newBuilder()
                .setUser(user)
                .setRequestId(request.getRequestId())
                .setStatus("SUCCESS")
                .setMessage("Usuario obtenido exitosamente")
                .build();
            
            log.info("gRPC: Usuario aleatorio obtenido - ID: {} - Nombre: {}", 
                    user.getId(), user.getNombre());
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC: Error obteniendo usuario aleatorio: {}", e.getMessage());
            
            GetRandomUserResponse errorResponse = GetRandomUserResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .setStatus("ERROR")
                .setMessage("Error obteniendo usuario: " + e.getMessage())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * Obtiene un usuario por ID
     */
    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        log.info("gRPC: Solicitando usuario por ID: {} - Request ID: {}", 
                request.getUserId(), request.getRequestId());
        
        try {
            // Obtener usuario por ID del servicio
            var userDto = userService.getUserById(request.getUserId());
            
            if (userDto != null) {
                // Mapear DTO a objeto gRPC
                User user = User.newBuilder()
                    .setId(userDto.getId())
                    .setNombre(userDto.getNombre())
                    .setEmail(userDto.getEmail())
                    .setTelefono(userDto.getTelefono())
                    .setDireccion(userDto.getDireccion())
                    .setCiudad(userDto.getCiudad())
                    .setPais(userDto.getPais())
                    .build();
                
                GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                    .setUser(user)
                    .setRequestId(request.getRequestId())
                    .setStatus("SUCCESS")
                    .setMessage("Usuario encontrado")
                    .build();
                
                log.info("gRPC: Usuario encontrado - ID: {} - Nombre: {}", 
                        user.getId(), user.getNombre());
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } else {
                GetUserByIdResponse notFoundResponse = GetUserByIdResponse.newBuilder()
                    .setRequestId(request.getRequestId())
                    .setStatus("NOT_FOUND")
                    .setMessage("Usuario no encontrado con ID: " + request.getUserId())
                    .build();
                
                log.warn("gRPC: Usuario no encontrado - ID: {}", request.getUserId());
                
                responseObserver.onNext(notFoundResponse);
                responseObserver.onCompleted();
            }
            
        } catch (Exception e) {
            log.error("gRPC: Error obteniendo usuario por ID: {}", e.getMessage());
            
            GetUserByIdResponse errorResponse = GetUserByIdResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .setStatus("ERROR")
                .setMessage("Error obteniendo usuario: " + e.getMessage())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
    
    /**
     * Obtiene todos los usuarios
     */
    @Override
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<GetAllUsersResponse> responseObserver) {
        log.info("gRPC: Solicitando todos los usuarios - Request ID: {}", request.getRequestId());
        
        try {
            // Obtener todos los usuarios del servicio
            List<cliente.application.dto.UserResponse> usersDto = userService.getAllUsers();
            
            // Mapear DTOs a objetos gRPC
            List<User> users = usersDto.stream()
                .map(userDto -> User.newBuilder()
                    .setId(userDto.getId())
                    .setNombre(userDto.getNombre())
                    .setEmail(userDto.getEmail())
                    .setTelefono(userDto.getTelefono())
                    .setDireccion(userDto.getDireccion())
                    .setCiudad(userDto.getCiudad())
                    .setPais(userDto.getPais())
                    .build())
                .collect(Collectors.toList());
            
            GetAllUsersResponse response = GetAllUsersResponse.newBuilder()
                .addAllUsers(users)
                .setRequestId(request.getRequestId())
                .setStatus("SUCCESS")
                .setMessage("Usuarios obtenidos exitosamente")
                .build();
            
            log.info("gRPC: {} usuarios obtenidos", users.size());
            
            responseObserver.onNext(response);
            responseObserver.onCompleted();
            
        } catch (Exception e) {
            log.error("gRPC: Error obteniendo usuarios: {}", e.getMessage());
            
            GetAllUsersResponse errorResponse = GetAllUsersResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .setStatus("ERROR")
                .setMessage("Error obteniendo usuarios: " + e.getMessage())
                .build();
            
            responseObserver.onNext(errorResponse);
            responseObserver.onCompleted();
        }
    }
}
