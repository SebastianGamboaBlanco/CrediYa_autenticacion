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
@Schema(description = "Respuesta de operaciones sobre usuarios")
public class UsuarioResponse {

    @Schema(description = "Código de estado HTTP", example = "200")
    private String status;
    
    @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario registrado exitosamente")
    private String mensaje;


    public static UsuarioResponse success(String mensaje) {
        return new UsuarioResponse("200", mensaje);
    }

    public static UsuarioResponse error(String mensaje) {
        return new UsuarioResponse("400", mensaje);
    }
    
    public static UsuarioResponse conflict(String mensaje) {
        return new UsuarioResponse("409", mensaje);
    }
    
    public static UsuarioResponse internalError(String mensaje) {
        return new UsuarioResponse("500", mensaje);
    }

}