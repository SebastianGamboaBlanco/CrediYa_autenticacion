package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.validation.constraints.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Data for registering a new user")
public class UserRequest {
    
    @Schema(description = "User's first names", example = "Juan Carlos", required = true)
    @NotBlank(message = "First name is required")
    private String firstName;

    @Schema(description = "User's last names", example = "Pérez González", required = true)
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Schema(description = "User's email", example = "juan.perez@email.com", required = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Email format is not valid")
    private String email;

    @Schema(description = "Identity document number", example = "12345678", required = true)
    @NotBlank(message = "Identity document is required")
    private String documentIdentity;

    @Schema(description = "Birth date in MM-dd-yyyy format", example = "05-15-1990")
    private String birthDate;

    @Schema(description = "Phone number", example = "3001234567")
    private String phone;

    @Schema(description = "User role ID", example = "1", required = true)
    @NotNull(message = "Role is required")
    private Long roleId;

    @Schema(description = "User's base salary", example = "3500000", required = true, minimum = "0", maximum = "15000000")
    @NotNull(message = "Base salary is required")
    private Integer baseSalary;

}