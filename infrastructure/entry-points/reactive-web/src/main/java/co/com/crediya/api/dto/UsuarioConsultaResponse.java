package co.com.crediya.api.dto;

import co.com.crediya.model.UsuarioCompleto;
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
@Schema(description = "Respuesta de consulta de usuario")
public class UsuarioConsultaResponse {

    @Schema(description = "Código de respuesta: 0=éxito, 1=error", example = "0")
    private int code;
    
    @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario encontrado")
    private String mensaje;
    
    @Schema(description = "Datos del usuario encontrado")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UsuarioData usuario;
    
    @Schema(description = "ID de trazabilidad para seguimiento de errores", example = "abc123-def456")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String trace;


    public static UsuarioConsultaResponse success(UsuarioCompleto usuarioCompleto) {
        UsuarioData usuarioData = new UsuarioData(
                usuarioCompleto.getId(),
                usuarioCompleto.getNombres(),
                usuarioCompleto.getApellidos(),
                usuarioCompleto.getCorreoElectronico(),
                usuarioCompleto.getDocumentoIdentidad(),
                usuarioCompleto.getFechaNacimiento(),
                usuarioCompleto.getTelefono(),
                usuarioCompleto.getIdRol(),
                usuarioCompleto.getSalarioBase()
        );
        
        return new UsuarioConsultaResponse(0, "Usuario encontrado", usuarioData, null);
    }

    public static UsuarioConsultaResponse notFound(String documentoIdentidad) {
        return new UsuarioConsultaResponse(1, 
                "Usuario no encontrado con documento: " + documentoIdentidad, null, TraceUtils.getCurrentTrace());
    }
    
    public static UsuarioConsultaResponse error(String mensaje) {
        return new UsuarioConsultaResponse(1, mensaje, null, TraceUtils.getCurrentTrace());
    }

}