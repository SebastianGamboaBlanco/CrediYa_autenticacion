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
@Schema(description = "Error específico de un campo")
public class FieldError {
    @Schema(description = "Nombre del campo", example = "correoElectronico")
    private String campo;
    
    @Schema(description = "Mensaje de error", example = "El correo electrónico es requerido")
    private String mensaje;
    
    @Schema(description = "Valor rechazado", example = "correo-invalido")
    private Object valorRechazado;
}