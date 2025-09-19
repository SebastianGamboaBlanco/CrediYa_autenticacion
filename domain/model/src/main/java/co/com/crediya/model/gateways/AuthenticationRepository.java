package co.com.crediya.model.gateways;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.valueobjects.JwtToken;
import reactor.core.publisher.Mono;

public interface AuthenticationRepository {
    
    Mono<CompleteUser> findUserByEmailForAuthentication(String email);
    
    Mono<Boolean> validateUserPassword(String email, String plainPassword);

    
}