package co.com.crediya.model.gateways;

import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.model.valueobjects.TokenClaims;
import reactor.core.publisher.Mono;

public interface JwtTokenService {
    
    Mono<JwtToken> generateToken(TokenClaims claims);
    
    Mono<TokenClaims> validateAndParseClaims(JwtToken token);
    
    Mono<Boolean> isTokenValid(JwtToken token);

    
}