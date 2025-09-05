package co.com.crediya.api.processor;

import co.com.crediya.api.dto.LoginRequest;
import co.com.crediya.api.dto.LoginResponse;
import co.com.crediya.api.helper.ValidationUtils;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.usecase.AutenticacionUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Slf4j
@Component
@RequiredArgsConstructor
public class AutenticacionProcessor {
    
    private final AutenticacionUseCase autenticacionUseCase;
    private final ValidationUtils validationUtils;

    public Mono<LoginResponse> procesarAutenticacion(LoginRequest request) {
        MDC.put("operation", "login");
        MDC.put("email", request.getEmail());
        
        log.info("INICIO - Procesando autenticación - Email: {}", request.getEmail());
        
        return validateLoginRequest(request)
                .doOnNext(req -> log.debug("Validaciones Bean Validation completadas"))
                .flatMap(this::authenticateUser)
                .flatMap(this::buildLoginResponse)
                .doOnSuccess(response -> 
                    log.info("FIN EXITOSO - Autenticación completada - Email: {}", request.getEmail()))
                .doOnError(error -> 
                    log.error("FIN CON ERROR - Error procesando autenticación - Email: {}, Tipo: {}, Mensaje: {}", 
                            request.getEmail(), error.getClass().getSimpleName(), error.getMessage()));
    }

    private Mono<LoginRequest> validateLoginRequest(LoginRequest request) {
        return validationUtils.validateRequest(request);
    }

    private Mono<JwtToken> authenticateUser(LoginRequest request) {
        return autenticacionUseCase.authenticate(request.getEmail(), request.getPassword())
                .doOnNext(token -> log.info("Token JWT generado exitosamente para usuario: {}", request.getEmail()))
                .doOnError(error -> log.error("Error en autenticación para usuario: {}", request.getEmail(), error));
    }

    private Mono<LoginResponse> buildLoginResponse(JwtToken jwtToken) {
        return autenticacionUseCase.extractClaims(jwtToken)
                .map(claims -> LoginResponse.success(
                        jwtToken.getValue(),
                        jwtToken.getExpiresAt(),
                        claims.getRole().getId(),
                        claims.getRole().getName()
                ));
    }
}