package cliente.application.config;

import com.atomikos.icatch.jta.UserTransactionImp;
import com.atomikos.icatch.jta.UserTransactionManager;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.UserTransaction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.jta.JtaTransactionManager;

@Configuration
public class JtaConfig {

  @Bean(name = "atomikosUserTransaction")
  public UserTransaction atomikosUserTransaction() throws Exception {
    UserTransactionImp utx = new UserTransactionImp();
    utx.setTransactionTimeout(300);
    return utx;
  }

  @Bean(name = "atomikosTransactionManager", initMethod = "init", destroyMethod = "close")
  public UserTransactionManager atomikosTransactionManager() {
    UserTransactionManager utm = new UserTransactionManager();
    utm.setForceShutdown(false);
    return utm;
  }

  // OJO: NO lo llames "transactionManager" (deja el nombre "jtaTransactionManager")
  @Bean(name = "jtaTransactionManager")
  @DependsOn({"atomikosUserTransaction", "atomikosTransactionManager"})
  public PlatformTransactionManager jtaTransactionManager(
      UserTransaction atomikosUserTransaction,
      TransactionManager atomikosTransactionManager) {
    return new JtaTransactionManager(atomikosUserTransaction, atomikosTransactionManager);
  }
}
