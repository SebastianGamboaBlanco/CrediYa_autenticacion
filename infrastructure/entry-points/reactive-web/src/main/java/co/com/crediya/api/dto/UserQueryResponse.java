package co.com.crediya.api.dto;

import co.com.crediya.model.CompleteUser;
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
@Schema(description = "User query response")
public class UserQueryResponse {

    @Schema(description = "Response code: 0=success, 1=error", example = "0")
    private int code;
    
    @Schema(description = "Descriptive message of the operation", example = "User found")
    private String message;
    
    @Schema(description = "Found user data")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserData user;
    
    @Schema(description = "Trace ID for error tracking", example = "abc123-def456")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;


    public static UserQueryResponse success(CompleteUser completeUser) {
        UserData userData = new UserData(
                completeUser.getId(),
                completeUser.getFirstName(),
                completeUser.getLastName(),
                completeUser.getEmail(),
                completeUser.getDocumentIdentity(),
                completeUser.getBirthDate(),
                completeUser.getPhone(),
                completeUser.getRoleId(),
                completeUser.getBaseSalary()
        );
        
        return new UserQueryResponse(0, "User found", userData, null);
    }

    public static UserQueryResponse notFound(String documentIdentity) {
        return new UserQueryResponse(1,
                "User not found with document: " + documentIdentity, null, TraceUtils.getCurrentTrace());
    }

    public static UserQueryResponse notFoundByEmail(String email) {
        return new UserQueryResponse(1,
                "User not found with email: " + email, null, TraceUtils.getCurrentTrace());
    }

    public static UserQueryResponse error(String message) {
        return new UserQueryResponse(1, message, null, TraceUtils.getCurrentTrace());
    }

}