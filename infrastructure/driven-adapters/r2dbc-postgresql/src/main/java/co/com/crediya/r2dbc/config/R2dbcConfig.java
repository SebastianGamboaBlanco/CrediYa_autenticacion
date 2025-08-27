package co.com.crediya.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;

@Configuration
@EnableConfigurationProperties(PostgresqlConnectionProperties.class)
@EnableR2dbcRepositories(basePackages = "co.com.crediya.r2dbc")
@EnableTransactionManagement
public class R2dbcConfig extends AbstractR2dbcConfiguration {

    private final ConnectionPool connectionPool;

    public R2dbcConfig(ConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    @Override
    public ConnectionPool connectionFactory() {
        return connectionPool;
    }

    @Bean
    public ReactiveTransactionManager transactionManager() {
        return new R2dbcTransactionManager(connectionPool);
    }
}