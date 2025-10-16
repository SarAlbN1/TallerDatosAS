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
    basePackages = "cliente.application.repositories.pagos",
    entityManagerFactoryRef = "pagosEntityManagerFactory",
    transactionManagerRef = "jtaTransactionManager"
)
public class PagosJpaConfig {

    @Bean(name = "pagosEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean pagosEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("pagosDataSource") DataSource dataSource) {
        
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "validate");
        properties.put("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        properties.put("hibernate.show_sql", true);
        properties.put("hibernate.format_sql", true);

        
        return builder
                .dataSource(dataSource)
                .packages("cliente.application.models.pagos")
                .persistenceUnit("pagos")
                .properties(properties)
                .jta(true)
                .build();
    }
}