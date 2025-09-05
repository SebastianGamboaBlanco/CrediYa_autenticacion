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

    @JsonProperty("mensaje")
    private String mensaje;

    public static AuthErrorResponse unauthorized(String mensaje) {
        return AuthErrorResponse.builder()
                .code(1)
                .mensaje(mensaje)
                .build();
    }

    public static AuthErrorResponse tokenRequired() {
        return unauthorized("Token de acceso requerido");
    }

    public static AuthErrorResponse tokenInvalid() {
        return unauthorized("Token JWT inválido o expirado");
    }
}