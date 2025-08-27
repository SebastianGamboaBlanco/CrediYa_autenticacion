package co.com.crediya.r2dbc.config;

import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class PostgreSQLConnectionPool {
    private static final int DEFAULT_INITIAL_SIZE = 5;
    private static final int DEFAULT_MAX_SIZE = 10;
    private static final int DEFAULT_MAX_IDLE_TIME = 30;
    private static final int DEFAULT_PORT = 5432;

    @Bean
    public ConnectionPool getConnectionConfig(PostgresqlConnectionProperties properties) {
        PostgresqlConnectionConfiguration dbConfiguration = PostgresqlConnectionConfiguration.builder()
                .host(properties.host())
                .port(properties.port() != null ? properties.port() : DEFAULT_PORT)
                .database(properties.database())
                .schema(properties.schema())
                .username(properties.username())
                .password(properties.password())
                .build();

        ConnectionPoolConfiguration poolConfiguration = ConnectionPoolConfiguration.builder()
                .connectionFactory(new PostgresqlConnectionFactory(dbConfiguration))
                .name("crediya-postgres-connection-pool")
                .initialSize(properties.initialSize() != null ? properties.initialSize() : DEFAULT_INITIAL_SIZE)
                .maxSize(properties.maxSize() != null ? properties.maxSize() : DEFAULT_MAX_SIZE)
                .maxIdleTime(Duration.ofMinutes(properties.maxIdleTimeMinutes() != null ? 
                           properties.maxIdleTimeMinutes() : DEFAULT_MAX_IDLE_TIME))
                .validationQuery("SELECT 1")
                .build();

        return new ConnectionPool(poolConfiguration);
    }
}