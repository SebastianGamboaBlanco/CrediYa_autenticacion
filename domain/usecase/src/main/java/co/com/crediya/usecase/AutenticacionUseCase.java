package co.com.crediya.usecase;

import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.AutenticacionRepository;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.model.valueobjects.TokenClaims;
import reactor.core.publisher.Mono;

import java.time.Instant;

public class AutenticacionUseCase {

    private final AutenticacionRepository authenticationRepository;
    private final JwtTokenService jwtTokenService;

    public AutenticacionUseCase(AutenticacionRepository authenticationRepository, JwtTokenService jwtTokenService) {
        this.authenticationRepository = authenticationRepository;
        this.jwtTokenService = jwtTokenService;
    }

    public Mono<JwtToken> authenticate(String email, String password) {
        return validateCredentials(email, password)
                .flatMap(this::generateToken);

    }

    private Mono<UsuarioCompleto> validateCredentials(String email, String password) {
        return authenticationRepository.findUserByEmailForAuthentication(email)
                .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.CORREO_NOT_EXISTS, email)))
                .flatMap(user -> authenticationRepository.validateUserPassword(email, password)
                        .filter(isValid -> isValid)
                        .switchIfEmpty(Mono.error(new BusinessException(ErrorCode.PASSWORD_INVALID)))
                        .thenReturn(user));
    }

    private Mono<JwtToken> generateToken(UsuarioCompleto usuario) {
        Instant now = Instant.now();
        Instant accessTokenExpiration = now.plusSeconds(60 * 60);

        TokenClaims claims = TokenClaims.builder()
                .subject(usuario.getId().toString())
                .email(usuario.getCorreoElectronico())
                .role(usuario.getRole())
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