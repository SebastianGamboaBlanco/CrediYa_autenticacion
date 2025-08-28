package co.com.crediya.api.dto;

import co.com.crediya.model.UsuarioCompleto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de consulta de usuario")
public class UsuarioConsultaResponse {

    @Schema(description = "Código de estado HTTP", example = "200")
    private String status;
    
    @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario encontrado")
    private String mensaje;
    
    @Schema(description = "Datos del usuario encontrado")
    private UsuarioData usuario;


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
        
        return new UsuarioConsultaResponse("200", "Usuario encontrado", usuarioData);
    }

    public static UsuarioConsultaResponse notFound(String documentoIdentidad) {
        return new UsuarioConsultaResponse("404", 
                "Usuario no encontrado con documento: " + documentoIdentidad, null);
    }
    
    public static UsuarioConsultaResponse error(String mensaje) {
        return new UsuarioConsultaResponse("400", mensaje, null);
    }

}