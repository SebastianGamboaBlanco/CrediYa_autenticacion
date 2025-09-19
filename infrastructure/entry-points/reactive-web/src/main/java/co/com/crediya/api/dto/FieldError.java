package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Specific field error")
public class FieldError {
    @Schema(description = "Field name", example = "email")
    private String field;
    
    @Schema(description = "Error message", example = "Email is required")
    private String message;
    
    @Schema(description = "Rejected value", example = "invalid-email")
    private Object rejectedValue;
}