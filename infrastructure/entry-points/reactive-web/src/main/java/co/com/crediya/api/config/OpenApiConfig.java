package co.com.crediya.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("CrediYa - API de Autenticación")
                        .description("API REST para el manejo de usuarios y autenticación en el sistema CrediYa")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Equipo CrediYa")
                                .email("soporte@crediya.com")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Servidor de desarrollo"),
                        new Server()
                                .url("https://api.crediya.com")
                                .description("Servidor de producción")
                ));
    }
}