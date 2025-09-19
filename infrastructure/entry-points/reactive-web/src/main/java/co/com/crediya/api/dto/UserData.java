package co.com.crediya.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Complete user data")
public class UserData {
    @Schema(description = "Unique user ID", example = "1")
    private Long id;
    
    @Schema(description = "User's first names", example = "Juan Carlos")
    private String firstName;
    
    @Schema(description = "User's last names", example = "Pérez González")
    private String lastName;
    
    @Schema(description = "User's email", example = "juan.perez@email.com")
    private String email;
    
    @Schema(description = "Identity document number", example = "12345678")
    private String documentIdentity;
    
    @Schema(description = "Birth date", example = "1990-05-15")
    private LocalDate birthDate;
    
    @Schema(description = "Phone number", example = "3001234567")
    private String phone;
    
    @Schema(description = "User role ID", example = "1")
    private Long roleId;
    
    @Schema(description = "User's base salary", example = "3500000")
    private Integer baseSalary;
}