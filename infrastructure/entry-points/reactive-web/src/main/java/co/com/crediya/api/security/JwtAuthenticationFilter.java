package co.com.crediya.api.security;

import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.usecase.AutenticacionUseCase;
import co.com.crediya.model.valueobjects.TokenClaims;
import co.com.crediya.api.exception.ErrorHandler;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

    private final AutenticacionUseCase authenticationUseCase;
    private final ErrorHandler errorHandler;
    
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/v1/login",
        "/actuator",
        "/swagger-ui",
        "/v3/api-docs",
        "/webjars"
    );

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        
        if (shouldSkipAuthentication(path)) {
            return chain.filter(exchange);
        }

        return extractTokenFromRequest(exchange)
                .flatMap(this::validateToken)
                .flatMap(claims -> {
                    exchange.getAttributes().put("user.id", claims.getSubject());
                    exchange.getAttributes().put("user.email", claims.getEmail());
                    exchange.getAttributes().put("user.role", claims.getRole());
                    return chain.filter(exchange);
                })
                .onErrorResume(error -> errorHandler.handleJwtAuthenticationError(exchange, error));
    }

    private boolean shouldSkipAuthentication(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(path::startsWith);
    }

    private Mono<JwtToken> extractTokenFromRequest(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("Token JWT no encontrado o formato inválido");
            }
            
            String token = authHeader.substring(7);
            return JwtToken.fromString(token);
        });
    }

    private Mono<TokenClaims> validateToken(JwtToken token) {
        return authenticationUseCase.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new RuntimeException("Token JWT inválido o expirado"));
                    }
                    return authenticationUseCase.extractClaims(token);
                });
    }

}