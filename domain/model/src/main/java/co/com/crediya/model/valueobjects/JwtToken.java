package co.com.crediya.model.valueobjects;

import java.time.Instant;
import java.util.Objects;

public class JwtToken {
    private final String value;
    private final Instant expiresAt;

    private JwtToken(String value, Instant expiresAt) {
        this.value = value;
        this.expiresAt = expiresAt;
    }

    public static JwtToken of(String token, Instant expiresAt) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }
        if (expiresAt == null) {
            throw new IllegalArgumentException("Token expiration time cannot be null");
        }
        if (expiresAt.isBefore(Instant.now())) {
            throw new IllegalArgumentException("Token cannot be expired at creation");
        }
        
        return new JwtToken(token.trim(), expiresAt);
    }

    public static JwtToken fromString(String token) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("JWT token cannot be null or empty");
        }
        
        return new JwtToken(token.trim(), null);
    }

    public String getValue() {
        return value;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public boolean hasExpiration() {
        return expiresAt != null;
    }

    public boolean isValid() {
        return value != null && !value.trim().isEmpty() && !isExpired();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JwtToken jwtToken = (JwtToken) o;
        return Objects.equals(value, jwtToken.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }


    @Override
    public String toString() {
        return value;
    }
}