package co.com.crediya.usecase;

import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.AuthenticationRepository;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.model.valueobjects.TokenClaims;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class AuthenticationUseCase {

    private final AuthenticationRepository authenticationRepository;
    private final JwtTokenService jwtTokenService;

    public AuthenticationUseCase(AuthenticationRepository authenticationRepository, JwtTokenService jwtTokenService) {
        this.authenticationRepository = authenticationRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public Mono<JwtToken> authenticate(String email, String password) {
        return validateCredentials(email, password)
                .flatMap(this::generateToken);

    }

    private Mono<CompleteUser> validateCredentials(String email, String password) {
        return authenticationRepository.findUserByEmailForAuthentication(email)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.EMAIL_NOT_EXISTS, email)))
                .flatMap(user -> authenticationRepository.validateUserPassword(email, password)
                        .filter(Boolean.TRUE::equals)
                        .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.INVALID_PASSWORD)))
                        .thenReturn(user));
    }

    private Mono<JwtToken> generateToken(CompleteUser user) {
        Instant now = Instant.now();
        Instant accessTokenExpiration = now.plusSeconds(60 * 60);

        TokenClaims claims = TokenClaims.builder()
                .document(user.getDocumentIdentity())
                .email(user.getEmail())
                .role(user.getRole())
                .issuedAt(now)
                .expiresAt(accessTokenExpiration)
                .issuer("crediya-auth-service")
                .build();

        return jwtTokenService.generateToken(claims);
    }

    public Mono<Boolean> validateToken(JwtToken token) {
        return jwtTokenService.isTokenValid(token);
    }

    public Mono<TokenClaims> extractClaims(JwtToken token) {
        return jwtTokenService.validateAndParseClaims(token);
    }
}