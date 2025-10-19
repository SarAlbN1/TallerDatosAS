package cliente.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración de OpenAPI/Swagger para documentación de la API
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Sistema de Compras Distribuidas API")
                .description("API REST para sistema de e-commerce distribuido con transacciones JTA")
                .version("1.0.0")
                .contact(new Contact()
                    .name("Equipo de Desarrollo")
                    .email("equipo@empresa.com")
                    .url("https://empresa.com"))
                .license(new License()
                    .name("MIT License")
                    .url("https://opensource.org/licenses/MIT")))
            .servers(List.of(
                new Server()
                    .url("http://localhost:8080")
                    .description("Servidor de desarrollo"),
                new Server()
                    .url("https://api.empresa.com")
                    .description("Servidor de producción")
            ));
    }
}
