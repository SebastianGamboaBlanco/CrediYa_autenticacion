package co.com.crediya.config;

import co.com.crediya.model.gateways.AutenticacionRepository;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.usecase.AutenticacionUseCase;
import co.com.crediya.usecase.RegistrarUsuarioUseCase;
import co.com.crediya.usecase.ConsultarUsuarioUseCase;
import co.com.crediya.model.gateways.UsuarioRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

@Configuration
@ComponentScan(basePackages = "co.com.crediya.usecase",
        includeFilters = {
                @ComponentScan.Filter(type = FilterType.REGEX, pattern = "^.+UseCase$")
        },
        useDefaultFilters = false)
public class UseCasesConfig {

    @Bean  
    public RegistrarUsuarioUseCase registrarUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        return new RegistrarUsuarioUseCase(usuarioRepository, passwordService);
    }

    @Bean
    public ConsultarUsuarioUseCase consultarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        return new ConsultarUsuarioUseCase(usuarioRepository);
    }

    @Bean
    public AutenticacionUseCase autenticacionUseCase(AutenticacionRepository autenticacionRepository, JwtTokenService jwtTokenService) {
        return new AutenticacionUseCase(autenticacionRepository, jwtTokenService);
    }
}
