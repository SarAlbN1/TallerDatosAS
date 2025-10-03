package cliente.application.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
public class JtaConfig {

    @Bean(name = "transactionManager")
    public JtaTransactionManager jtaTransactionManager() {
        // Usa el Transaction Manager de Atomikos presente en el classpath
        return new JtaTransactionManager();
    }
}
