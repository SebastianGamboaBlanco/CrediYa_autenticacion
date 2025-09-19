package co.com.crediya.api.processor;

import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.helper.ValidationUtils;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.usecase.AuthenticationUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationProcessor {
    
    private final AuthenticationUseCase authenticationUseCase;
    private final ValidationUtils validationUtils;

    public Mono<LoginResponse> processAuthentication(LoginRequest request) {
        MDC.put("operation", "login");
        MDC.put("email", request.getEmail());
        
        log.info("START - Processing authentication - Email: {}", request.getEmail());
        
        return validateLoginRequest(request)
                .doOnNext(req -> log.debug("Bean Validation validations completed"))
                .flatMap(this::authenticateUser)
                .flatMap(this::buildLoginResponse)
                .doOnSuccess(response -> 
                    log.info("SUCCESSFUL END - Authentication completed - Email: {}", request.getEmail()))
                .doOnError(error -> 
                    log.error("ERROR END - Error processing authentication - Email: {}, Type: {}, Message: {}", 
                            request.getEmail(), error.getClass().getSimpleName(), error.getMessage()));
    }

    private Mono<LoginRequest> validateLoginRequest(LoginRequest request) {
        return validationUtils.validateRequest(request);
    }

    private Mono<JwtToken> authenticateUser(LoginRequest request) {
        return authenticationUseCase.authenticate(request.getEmail(), request.getPassword())
                .doOnNext(token -> log.info("JWT token generated successfully for user: {}", request.getEmail()))
                .doOnError(error -> log.error("Authentication error for user: {}", request.getEmail(), error));
    }

    private Mono<LoginResponse> buildLoginResponse(JwtToken jwtToken) {
        return authenticationUseCase.extractClaims(jwtToken)
                .map(claims -> LoginResponse.success(
                        jwtToken.getValue(),
                        jwtToken.getExpiresAt(),
                        claims.getRole().getId(),
                        claims.getRole().getName()
                ));
    }
}