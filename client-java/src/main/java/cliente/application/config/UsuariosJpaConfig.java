package cliente.application.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "cliente.application.repositories.usuarios",
    entityManagerFactoryRef = "usuariosEntityManagerFactory",
    transactionManagerRef = "usuariosTransactionManager"
)
public class UsuariosJpaConfig {

    @Bean(name = "usuariosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean usuariosEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("usuariosDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);
        
        return builder
                .dataSource(dataSource)
                .packages("cliente.application.models.usuarios")
                .persistenceUnit("usuarios")
                .properties(properties)
                .jta(false) // NO usar JTA - base de datos no transaccional
                .build();
    }

    @Bean(name = "usuariosTransactionManager")
    public PlatformTransactionManager usuariosTransactionManager(
            @Qualifier("usuariosEntityManagerFactory") EntityManagerFactory usuariosEntityManagerFactory) {
        return new JpaTransactionManager(usuariosEntityManagerFactory);
    }
}
