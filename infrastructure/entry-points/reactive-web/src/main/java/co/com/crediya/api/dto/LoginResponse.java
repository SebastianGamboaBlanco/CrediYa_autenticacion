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
public class LoginResponse {

    @JsonProperty("code")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer code;

    @JsonProperty("message")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonProperty("accessToken")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String accessToken;

    @JsonProperty("expiresAt")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant expiresAt;

    @JsonProperty("role")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private RoleInfo role;

    public static LoginResponse success(String token, Instant expiresAt, Long roleId, String roleName) {
        return LoginResponse.builder()
                .code(0)
                .message("Login success")
                .accessToken(token)
                .expiresAt(expiresAt)
                .role(RoleInfo.builder()
                        .id(roleId)
                        .name(roleName)
                        .build())
                .build();
    }

    public static LoginResponse error(String message) {
        return LoginResponse.builder()
                .code(1)
                .message(message)
                .build();
    }
}