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
@Schema(description = "Datos para registrar un nuevo usuario")
public class UsuarioRequest {
    
    @Schema(description = "Nombres del usuario", example = "Juan Carlos", required = true)
    @NotBlank(message = "El nombre es obligatorio")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "Pérez González", required = true)
    @NotBlank(message = "Los apellidos son obligatorios")
    private String apellidos;

    @Schema(description = "Correo electrónico del usuario", example = "juan.perez@email.com", required = true)
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El formato del correo electrónico no es válido")
    private String correoElectronico;

    @Schema(description = "Número de documento de identidad", example = "12345678", required = true)
    @NotBlank(message = "El documento de identidad es obligatorio")
    private String documentoIdentidad;

    @Schema(description = "Fecha de nacimiento en formato yyyy-MM-dd", example = "1990-05-15")
    private String fechaNacimiento;

    @Schema(description = "Número de teléfono", example = "3001234567")
    private String telefono;

    @Schema(description = "ID del rol del usuario", example = "1", required = true)
    @NotNull(message = "El rol es obligatorio")
    private Long idRol;

    @Schema(description = "Salario base del usuario", example = "3500000", required = true, minimum = "0", maximum = "15000000")
    @NotNull(message = "El salario base es obligatorio")
    private Integer salarioBase;

}