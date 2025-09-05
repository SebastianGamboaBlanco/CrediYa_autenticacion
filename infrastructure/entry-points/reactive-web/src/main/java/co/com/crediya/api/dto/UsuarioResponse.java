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
@Schema(description = "Respuesta de operaciones sobre usuarios")
public class UsuarioResponse {

    @Schema(description = "Código de respuesta: 0=éxito, 1=error", example = "0")
    private int code;
    
    @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario registrado exitosamente")
    private String mensaje;
    
    @Schema(description = "ID de trazabilidad para seguimiento de errores", example = "abc123-def456")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;


    public static UsuarioResponse success(String mensaje) {
        return new UsuarioResponse(0, mensaje, null);
    }

    public static UsuarioResponse error(String mensaje) {
        return new UsuarioResponse(1, mensaje, TraceUtils.getCurrentTrace());
    }
    
    public static UsuarioResponse conflict(String mensaje) {
        return new UsuarioResponse(1, mensaje, TraceUtils.getCurrentTrace());
    }
    
    public static UsuarioResponse internalError(String mensaje) {
        return new UsuarioResponse(1, mensaje, TraceUtils.getCurrentTrace());
    }

}