package co.com.crediya.jwt;

import co.com.crediya.jwt.config.JwtProperties;
import co.com.crediya.model.gateways.JwtTokenService;
import co.com.crediya.model.valueobjects.JwtToken;
import co.com.crediya.model.valueobjects.Role;
import co.com.crediya.model.valueobjects.TokenClaims;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtServiceImpl implements JwtTokenService {

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;
    private final JwtParser jwtParser;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
        this.secretKey = Keys.hmacShaKeyFor(jwtProperties.getSecretKey().getBytes());
        this.jwtParser = Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build();
    }

    @Override
    public Mono<JwtToken> generateToken(TokenClaims claims) {
        return Mono.fromCallable(() -> {
            String token = Jwts.builder()
                    .setSubject(claims.getDocument())
                    .claim("email", claims.getEmail())
                    .claim("roleId", claims.getRole().getId())
                    .claim("roleName", claims.getRole().getName())
                    .setIssuedAt(Date.from(claims.getIssuedAt()))
                    .setExpiration(Date.from(claims.getExpiresAt()))
                    .setIssuer(claims.getIssuer())
                    .signWith(secretKey, SignatureAlgorithm.HS256)
                    .compact();

            return JwtToken.of(token, claims.getExpiresAt());
        });
    }

    @Override
    public Mono<TokenClaims> validateAndParseClaims(JwtToken token) {
        return Mono.fromCallable(() -> {
            Claims claims = jwtParser.parseClaimsJws(token.getValue()).getBody();
            
            Role role = Role.of(
                    claims.get("roleId", Long.class),
                    claims.get("roleName", String.class)
            );

            return TokenClaims.builder()
                    .document(claims.getSubject())
                    .email(claims.get("email", String.class))
                    .role(role)
                    .issuedAt(claims.getIssuedAt().toInstant())
                    .expiresAt(claims.getExpiration().toInstant())
                    .issuer(claims.getIssuer())
                    .build();
        })
        .onErrorMap(JwtException.class, ex -> new RuntimeException("Invalid JWT token: " + ex.getMessage()));
    }

    @Override
    public Mono<Boolean> isTokenValid(JwtToken token) {
        return Mono.fromCallable(() -> {
            try {
                jwtParser.parseClaimsJws(token.getValue());
                return !token.isExpired();
            } catch (JwtException e) {
                return false;
            }
        });
    }


}