package co.com.crediya.api.processor;

import co.com.crediya.api.dto.RoleInfo;
import co.com.crediya.api.dto.TokenValidationRequest;
import co.com.crediya.api.dto.TokenValidationResponse;
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
public class TokenValidationProcessor {

    private final AuthenticationUseCase authenticationUseCase;
    private final ValidationUtils validationUtils;

    public Mono<TokenValidationResponse> processTokenValidation(TokenValidationRequest request) {
        MDC.put("operation", "token-validation");

        log.info("START - Processing token validation");

        return validationUtils.validateRequest(request)
                .flatMap(this::validateAndExtractClaims)
                .flatMap(this::buildValidationResponse)
                .doOnSuccess(response ->
                        log.info("SUCCESSFUL END - Token validation completed - Valid: {}", response.getValid()))
                .doOnError(error ->
                        log.error("ERROR END - Error processing token validation - Type: {}, Message: {}",
                                error.getClass().getSimpleName(), error.getMessage()))
                .onErrorReturn(TokenValidationResponse.invalid("Invalid or expired token"));
    }

    private Mono<JwtToken> validateAndExtractClaims(TokenValidationRequest request) {
        JwtToken jwtToken = JwtToken.fromString(request.getToken());

        return authenticationUseCase.validateToken(jwtToken)
                .filter(Boolean.TRUE::equals)
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid token")))
                .thenReturn(jwtToken)
                .doOnNext(token -> log.info("Token validated successfully"))
                .doOnError(error -> log.error("Error validating token", error));
    }

    private Mono<TokenValidationResponse> buildValidationResponse(JwtToken jwtToken) {
        return authenticationUseCase.extractClaims(jwtToken)
                .map(claims -> {
                    RoleInfo roleInfo = RoleInfo.builder()
                            .id(claims.getRole().getId())
                            .name(claims.getRole().getName())
                            .build();

                    return TokenValidationResponse.valid(
                            claims.getDocument(),
                            claims.getEmail(),
                            roleInfo,
                            claims.getIssuedAt(),
                            claims.getExpiresAt(),
                            claims.getIssuer()
                    );
                });
    }
}