package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import co.com.crediya.api.helper.TraceUtils;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response for user operations")
public class UserResponse {

    @Schema(description = "Response code: 0=success, 1=error", example = "0")
    private int code;
    
    @Schema(description = "Descriptive message of the operation", example = "User registered successfully")
    private String message;
    
    @Schema(description = "Trace ID for error tracking", example = "abc123-def456")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;


    public static UserResponse success(String message) {
        return new UserResponse(0, message, null);
    }

    public static UserResponse error(String message) {
        return new UserResponse(1, message, TraceUtils.getCurrentTrace());
    }
    
    public static UserResponse conflict(String message) {
        return new UserResponse(1, message, TraceUtils.getCurrentTrace());
    }
    
    public static UserResponse internalError(String message) {
        return new UserResponse(1, message, TraceUtils.getCurrentTrace());
    }

}