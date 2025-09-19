package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import reactor.util.function.Tuple3;
import co.com.crediya.api.helper.TraceUtils;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Response with multiple validation errors")
public class ValidationErrorResponse {
    
    @Schema(description = "Response code: 0=success, 1=error", example = "1")
    private int code;
    
    @Schema(description = "List of specific errors")
    private List<FieldError> errors;
    
    @Schema(description = "Traceability ID for error tracking", example = "abc123-def456")
    private String trace;
    
    public static ValidationErrorResponse from(List<Tuple3<String, String, String>> validationErrors) {
        List<FieldError> fieldErrors = validationErrors.stream()
            .filter(tuple -> tuple != null)
            .map(tuple -> new FieldError(
                tuple.getT1(),
                tuple.getT2(), 
                tuple.getT3() != null && !tuple.getT3().equals("") ? tuple.getT3() : null
            ))
            .collect(Collectors.toList());
            
        return new ValidationErrorResponse(1, fieldErrors, TraceUtils.getCurrentTrace());
    }
}