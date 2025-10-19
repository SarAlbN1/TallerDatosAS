package cliente;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = "cliente.application")
public class ClientJavaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ClientJavaApplication.class, args);
	}

}
