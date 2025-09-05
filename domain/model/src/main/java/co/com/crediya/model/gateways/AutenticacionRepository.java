package co.com.crediya.model.gateways;

import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.valueobjects.JwtToken;
import reactor.core.publisher.Mono;

public interface AutenticacionRepository {
    
    Mono<UsuarioCompleto> findUserByEmailForAuthentication(String email);
    
    Mono<Boolean> validateUserPassword(String email, String plainPassword);

    
}