package cliente.application.config;

import io.grpc.ServerBuilder;
import net.devh.boot.grpc.server.config.GrpcServerProperties;
import net.devh.boot.grpc.server.serverfactory.GrpcServerConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuración de gRPC
 */
@Configuration
public class GrpcConfig {

    @Bean
    public GrpcServerConfigurer grpcServerConfigurer() {
        return serverBuilder -> {
            // Configurar el servidor gRPC
            if (serverBuilder instanceof ServerBuilder) {
                ((ServerBuilder<?>) serverBuilder)
                    .maxInboundMessageSize(4 * 1024 * 1024) // 4MB
                    .maxInboundMetadataSize(8 * 1024); // 8KB
            }
        };
    }
}
