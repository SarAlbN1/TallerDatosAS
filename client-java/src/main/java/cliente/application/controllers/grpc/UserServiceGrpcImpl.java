package cliente.application.controllers.grpc;

import cliente.application.models.usuarios.Usuario;
import cliente.application.services.usuarios.UsuarioService;
import cliente.grpc.user.*;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio gRPC de usuarios
 */
@GrpcService
@RequiredArgsConstructor
@Slf4j
public class UserServiceGrpcImpl extends UserServiceGrpc.UserServiceImplBase {
    
    private final UsuarioService userService;
    
    @Override
    public void getRandomUser(GetRandomUserRequest request, StreamObserver<GetRandomUserResponse> responseObserver) {
        java.util.List<Usuario> all = userService.getAllUsuarios();
        Usuario any = all.get((int)(System.currentTimeMillis() % all.size()));
        var dp = any.getDatosPersonales();
        String nombreCompleto = dp != null ? (dp.getNombre() + (dp.getApellido() != null ? (" " + dp.getApellido()) : "")) : any.getUsername();
        User user = User.newBuilder()
            .setId(any.getId())
            .setNombre(nombreCompleto)
            .setEmail(any.getEmail())
            .setTelefono(dp != null ? dp.getTelefono() : "")
            .setDireccion(dp != null ? dp.getDireccion() : "")
            .setCiudad(dp != null ? dp.getCiudad() : "")
            .setPais(dp != null ? dp.getPais() : "")
            .build();
        GetRandomUserResponse response = GetRandomUserResponse.newBuilder()
            .setUser(user)
            .setRequestId(request.getRequestId())
            .setStatus("SUCCESS")
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getUserById(GetUserByIdRequest request, StreamObserver<GetUserByIdResponse> responseObserver) {
        Optional<Usuario> userOpt = userService.getUsuarioById(request.getUserId());
        if (userOpt.isPresent()) {
            Usuario u = userOpt.get();
            var dp = u.getDatosPersonales();
            String nombre = dp != null ? (dp.getNombre() + (dp.getApellido() != null ? (" " + dp.getApellido()) : "")) : u.getUsername();
            User user = User.newBuilder()
                .setId(u.getId())
                .setNombre(nombre)
                .setEmail(u.getEmail())
                .setTelefono(dp != null ? dp.getTelefono() : "")
                .setDireccion(dp != null ? dp.getDireccion() : "")
                .setCiudad(dp != null ? dp.getCiudad() : "")
                .setPais(dp != null ? dp.getPais() : "")
                .build();
            GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                .setUser(user)
                .setRequestId(request.getRequestId())
                .setStatus("SUCCESS")
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        } else {
            GetUserByIdResponse response = GetUserByIdResponse.newBuilder()
                .setRequestId(request.getRequestId())
                .setStatus("NOT_FOUND")
                .build();
            responseObserver.onNext(response);
            responseObserver.onCompleted();
        }
    }

    @Override
    public void getAllUsers(GetAllUsersRequest request, StreamObserver<GetAllUsersResponse> responseObserver) {
        List<Usuario> all = userService.getAllUsuarios();
        List<User> users = all.stream().map(u -> {
            var dp = u.getDatosPersonales();
            String nombre = dp != null ? (dp.getNombre() + (dp.getApellido() != null ? (" " + dp.getApellido()) : "")) : u.getUsername();
            return User.newBuilder()
                .setId(u.getId())
                .setNombre(nombre)
                .setEmail(u.getEmail())
                .setTelefono(dp != null ? dp.getTelefono() : "")
                .setDireccion(dp != null ? dp.getDireccion() : "")
                .setCiudad(dp != null ? dp.getCiudad() : "")
                .setPais(dp != null ? dp.getPais() : "")
                .build();
        }).collect(Collectors.toList());
        GetAllUsersResponse response = GetAllUsersResponse.newBuilder()
            .addAllUsers(users)
            .setRequestId(request.getRequestId())
            .setStatus("SUCCESS")
            .build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}


