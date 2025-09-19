package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenValidationResponse {

    @JsonProperty("code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;

    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonProperty("valid")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean valid;

    @JsonProperty("subject")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String subject;

    @JsonProperty("email")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String email;

    @JsonProperty("role")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoleInfo role;

    @JsonProperty("issuedAt")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant issuedAt;

    @JsonProperty("expiresAt")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant expiresAt;

    @JsonProperty("issuer")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String issuer;

    public static TokenValidationResponse valid(String subject, String email, RoleInfo role, 
                                                Instant issuedAt, Instant expiresAt, String issuer) {
        return TokenValidationResponse.builder()
                .code(0)
                .message("Token valid")
                .valid(true)
                .subject(subject)
                .email(email)
                .role(role)
                .issuedAt(issuedAt)
                .expiresAt(expiresAt)
                .issuer(issuer)
                .build();
    }

    public static TokenValidationResponse invalid(String message) {
        return TokenValidationResponse.builder()
                .code(1)
                .message(message)
                .valid(false)
                .build();
    }
}