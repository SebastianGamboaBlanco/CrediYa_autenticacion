package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthErrorResponse {

    @JsonProperty("code")
    private Integer code;

    @JsonProperty("message")
    private String message;

    public static AuthErrorResponse unauthorized(String message) {
        return AuthErrorResponse.builder()
                .code(1)
                .message(message)
                .build();
    }

    public static AuthErrorResponse tokenRequired() {
        return unauthorized("Access token required");
    }

    public static AuthErrorResponse tokenInvalid() {
        return unauthorized("Token JWT invalid o expired");
    }
}