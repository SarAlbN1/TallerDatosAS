package cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@ComponentScan(
    basePackages = "cliente",
    excludeFilters = {
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "cliente\\.application\\.(controllers|services)\\.(Category|Product|Organization).*"
        ),
        @ComponentScan.Filter(
            type = FilterType.REGEX,
            pattern = "cliente\\.soap\\..*"
        )
    }
)
public class ClientJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientJavaApplication.class, args);
	}

}
