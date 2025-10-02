package cliente.application.config;

import com.atomikos.jdbc.AtomikosDataSourceBean;
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
}