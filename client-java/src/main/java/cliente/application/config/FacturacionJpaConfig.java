package cliente.application.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableJpaRepositories(
    basePackages = "cliente.application.repositories.facturacion",
    entityManagerFactoryRef = "facturacionEntityManagerFactory",
    transactionManagerRef = "transactionManager"
)
public class FacturacionJpaConfig {

    @Bean(name = "facturacionEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean facturacionEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("facturacionDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        
        return builder
                .dataSource(dataSource)
                .packages("cliente.application.models.facturacion")
                .persistenceUnit("facturacion")
                .properties(properties)
                .jta(true)
                .build();
    }
}