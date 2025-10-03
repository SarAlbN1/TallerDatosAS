package cliente.application.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    // ================== XA (participan en JTA/Atomikos) ==================

    @Bean(name = "inventarioDataSource")
    @Primary
    public DataSource inventarioDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("inventarioDB");
        ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");

        Properties xa = new Properties();
        xa.setProperty("url", "jdbc:mysql://localhost:3306/inventario?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xa.setProperty("user", "equipo");
        xa.setProperty("password", "123456");
        xa.setProperty("pinGlobalTxToPhysicalConnection", "true");
        ds.setXaProperties(xa);

        ds.setMinPoolSize(3);
        ds.setMaxPoolSize(25);
        ds.setMaxLifetime(20_000);
        ds.setBorrowConnectionTimeout(30);
        ds.setTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "facturacionDataSource")
    public DataSource facturacionDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("facturacionDB");
        ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");

        Properties xa = new Properties();
        xa.setProperty("url", "jdbc:mysql://localhost:3307/facturacion?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xa.setProperty("user", "equipo");
        xa.setProperty("password", "123456");
        xa.setProperty("pinGlobalTxToPhysicalConnection", "true");
        ds.setXaProperties(xa);

        ds.setMinPoolSize(3);
        ds.setMaxPoolSize(25);
        ds.setMaxLifetime(20_000);
        ds.setBorrowConnectionTimeout(30);
        ds.setTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "pagosDataSource")
    public DataSource pagosDataSource() {
        AtomikosDataSourceBean ds = new AtomikosDataSourceBean();
        ds.setUniqueResourceName("pagosDB");
        ds.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");

        Properties xa = new Properties();
        xa.setProperty("url", "jdbc:mysql://localhost:3308/pagos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xa.setProperty("user", "equipo");
        xa.setProperty("password", "123456");
        xa.setProperty("pinGlobalTxToPhysicalConnection", "true");
        ds.setXaProperties(xa);

        ds.setMinPoolSize(3);
        ds.setMaxPoolSize(25);
        ds.setMaxLifetime(20_000);
        ds.setBorrowConnectionTimeout(30);
        ds.setTestQuery("SELECT 1");
        return ds;
    }

    // ================== NO-XA (fuera de JTA; uso CRUD/lectura) ==================

    @Bean(name = "productosDataSource")
    public DataSource productosDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3309/productos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password");
        ds.setUsername("equipo");
        ds.setPassword("123456");
        ds.setMinimumIdle(3);
        ds.setMaximumPoolSize(25);
        ds.setMaxLifetime(1_800_000); // 30 min
        ds.setConnectionTimeout(30_000);
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }

    @Bean(name = "usuariosDataSource")
    public DataSource usuariosDataSource() {
        HikariDataSource ds = new HikariDataSource();
        ds.setJdbcUrl("jdbc:mysql://localhost:3310/usuarios?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password");
        ds.setUsername("equipo");
        ds.setPassword("123456");
        ds.setMinimumIdle(3);
        ds.setMaximumPoolSize(25);
        ds.setMaxLifetime(1_800_000); // 30 min
        ds.setConnectionTimeout(30_000);
        ds.setConnectionTestQuery("SELECT 1");
        return ds;
    }
}
