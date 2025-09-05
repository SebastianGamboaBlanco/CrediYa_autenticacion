package co.com.crediya.model.valueobjects;

import java.time.Instant;
import java.util.Objects;

public class TokenClaims {
    private final String subject;
    private final String email;
    private final Role role;
    private final Instant issuedAt;
    private final Instant expiresAt;
    private final String issuer;

    private TokenClaims(Builder builder) {
        this.subject = builder.subject;
        this.email = builder.email;
        this.role = builder.role;
        this.issuedAt = builder.issuedAt;
        this.expiresAt = builder.expiresAt;
        this.issuer = builder.issuer;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getSubject() {
        return subject;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    public Instant getIssuedAt() {
        return issuedAt;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public String getIssuer() {
        return issuer;
    }

    public boolean isExpired() {
        return expiresAt != null && expiresAt.isBefore(Instant.now());
    }

    public boolean isValid() {
        return !isExpired() && 
               subject != null && !subject.trim().isEmpty() &&
               email != null && !email.trim().isEmpty() &&
               role != null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TokenClaims that = (TokenClaims) o;
        return Objects.equals(subject, that.subject) &&
               Objects.equals(email, that.email) &&
               Objects.equals(role, that.role);
    }

    @Override
    public int hashCode() {
        return Objects.hash(subject, email, role);
    }

    @Override
    public String toString() {
        return "TokenClaims{" +
               "subject='" + subject + '\'' +
               ", email='" + email + '\'' +
               ", role=" + role +
               ", issuedAt=" + issuedAt +
               ", expiresAt=" + expiresAt +
               '}';
    }

    public static class Builder {
        private String subject;
        private String email;
        private Role role;
        private Instant issuedAt;
        private Instant expiresAt;
        private String issuer;

        private Builder() {
            this.issuedAt = Instant.now();
        }

        public Builder subject(String subject) {
            this.subject = subject;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder role(Role role) {
            this.role = role;
            return this;
        }

        public Builder issuedAt(Instant issuedAt) {
            this.issuedAt = issuedAt;
            return this;
        }

        public Builder expiresAt(Instant expiresAt) {
            this.expiresAt = expiresAt;
            return this;
        }

        public Builder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public TokenClaims build() {
            if (subject == null || subject.trim().isEmpty()) {
                throw new IllegalArgumentException("Subject cannot be null or empty");
            }
            if (email == null || email.trim().isEmpty()) {
                throw new IllegalArgumentException("Email cannot be null or empty");
            }
            if (role == null) {
                throw new IllegalArgumentException("Role cannot be null");
            }
            if (expiresAt == null) {
                throw new IllegalArgumentException("ExpiresAt cannot be null");
            }
            if (expiresAt.isBefore(issuedAt)) {
                throw new IllegalArgumentException("ExpiresAt cannot be before issuedAt");
            }

            return new TokenClaims(this);
        }
    }
}