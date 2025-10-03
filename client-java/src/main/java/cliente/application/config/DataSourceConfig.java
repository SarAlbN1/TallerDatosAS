package cliente.application.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Properties;

@Configuration
public class DataSourceConfig {

    @Bean(name = "inventarioDataSource")
    @Primary
    public DataSource inventarioDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("inventarioDB");
        dataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        
        Properties xaProps = new Properties();
    xaProps.setProperty(
        "url",
        "jdbc:mysql://localhost:3306/inventario?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xaProps.setProperty("user", "equipo");
        xaProps.setProperty("password", "123456");
    xaProps.setProperty("pinGlobalTxToPhysicalConnection", "true");
        dataSource.setXaProperties(xaProps);
        
        dataSource.setMinPoolSize(3);
        dataSource.setMaxPoolSize(25);
        dataSource.setMaxLifetime(20000);
        dataSource.setBorrowConnectionTimeout(30);
        dataSource.setTestQuery("SELECT 1");
        
        return dataSource;
    }

    @Bean(name = "facturacionDataSource")
    public DataSource facturacionDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("facturacionDB");
        dataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        
        Properties xaProps = new Properties();
    xaProps.setProperty(
        "url",
        "jdbc:mysql://localhost:3307/facturacion?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xaProps.setProperty("user", "equipo");
        xaProps.setProperty("password", "123456");
    xaProps.setProperty("pinGlobalTxToPhysicalConnection", "true");
        dataSource.setXaProperties(xaProps);
        
        dataSource.setMinPoolSize(3);
        dataSource.setMaxPoolSize(25);
        dataSource.setMaxLifetime(20000);
        dataSource.setBorrowConnectionTimeout(30);
        dataSource.setTestQuery("SELECT 1");
        
        return dataSource;
    }

    @Bean(name = "pagosDataSource")
    public DataSource pagosDataSource() {
        AtomikosDataSourceBean dataSource = new AtomikosDataSourceBean();
        dataSource.setUniqueResourceName("pagosDB");
        dataSource.setXaDataSourceClassName("com.mysql.cj.jdbc.MysqlXADataSource");
        
        Properties xaProps = new Properties();
    xaProps.setProperty(
        "url",
        "jdbc:mysql://localhost:3308/pagos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password&pinGlobalTxToPhysicalConnection=true");
        xaProps.setProperty("user", "equipo");
        xaProps.setProperty("password", "123456");
    xaProps.setProperty("pinGlobalTxToPhysicalConnection", "true");
        dataSource.setXaProperties(xaProps);
        
        dataSource.setMinPoolSize(3);
        dataSource.setMaxPoolSize(25);
        dataSource.setMaxLifetime(20000);
        dataSource.setBorrowConnectionTimeout(30);
        dataSource.setTestQuery("SELECT 1");
        
        return dataSource;
    }

    @Bean(name = "productosDataSource")
    public DataSource productosDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3309/productos?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password");
        dataSource.setUsername("equipo");
        dataSource.setPassword("123456");
        
        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(25);
        dataSource.setMaxLifetime(1800000); // 30 minutes
        dataSource.setConnectionTimeout(30000); // 30 seconds
        dataSource.setConnectionTestQuery("SELECT 1");
        
        return dataSource;
    }

    @Bean(name = "usuariosDataSource")
    public DataSource usuariosDataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl("jdbc:mysql://localhost:3310/usuarios?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC&defaultAuthenticationPlugin=mysql_native_password");
        dataSource.setUsername("equipo");
        dataSource.setPassword("123456");
        
        dataSource.setMinimumIdle(3);
        dataSource.setMaximumPoolSize(25);
        dataSource.setMaxLifetime(1800000); // 30 minutes
        dataSource.setConnectionTimeout(30000); // 30 seconds
        dataSource.setConnectionTestQuery("SELECT 1");
        
        return dataSource;
    }
}