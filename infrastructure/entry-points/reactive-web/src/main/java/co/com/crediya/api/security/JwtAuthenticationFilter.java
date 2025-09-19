package co.com.crediya.api.security;

import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.usecase.AuthenticationUseCase;
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

    private final AuthenticationUseCase authenticationUseCase;
    private final ErrorHandler errorHandler;
    
    private static final List<String> EXCLUDED_PATHS = List.of(
        "/api/v1/login",
        "/api/v1/auth/validate",
        "/api/v1/users/{documentIdentity}",
        "/api/v1/email/{email}",
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
                    exchange.getAttributes().put("user.id", claims.getDocument());
                    exchange.getAttributes().put("user.email", claims.getEmail());
                    exchange.getAttributes().put("user.role", claims.getRole());
                    return chain.filter(exchange);
                })
                .onErrorResume(error -> errorHandler.handleJwtAuthenticationError(exchange, error));
    }

    private boolean shouldSkipAuthentication(String path) {
        return EXCLUDED_PATHS.stream().anyMatch(excludedPath -> {
            if (excludedPath.contains("{")) {
                String basePattern = excludedPath.substring(0, excludedPath.indexOf("{"));
                return path.startsWith(basePattern);
            }
            return path.startsWith(excludedPath);
        });
    }

    private Mono<JwtToken> extractTokenFromRequest(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
            
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                throw new RuntimeException("JWT token not found or invalid format");
            }
            
            String token = authHeader.substring(7);
            return JwtToken.fromString(token);
        });
    }

    private Mono<TokenClaims> validateToken(JwtToken token) {
        return authenticationUseCase.validateToken(token)
                .flatMap(isValid -> {
                    if (!isValid) {
                        return Mono.error(new RuntimeException("JWT token invalid or expired"));
                    }
                    return authenticationUseCase.extractClaims(token);
                });
    }

}