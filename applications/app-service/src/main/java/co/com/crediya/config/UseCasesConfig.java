package co.com.crediya.config;

import co.com.crediya.model.gateways.AuthenticationRepository;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.usecase.AuthenticationUseCase;
import co.com.crediya.usecase.RegisterUserUseCase;
import co.com.crediya.usecase.GetUserUseCase;
import co.com.crediya.model.gateways.UserRepository;
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
    public RegisterUserUseCase registerUserUseCase(UserRepository userRepository, PasswordService passwordService) {
        return new RegisterUserUseCase(userRepository, passwordService);
    }

    @Bean
    public GetUserUseCase getUserUseCase(UserRepository userRepository) {
        return new GetUserUseCase(userRepository);
    }

    @Bean
    public AuthenticationUseCase authenticationUseCase(AuthenticationRepository authenticationRepository, JwtTokenService jwtTokenService) {
        return new AuthenticationUseCase(authenticationRepository, jwtTokenService);
    }
}
