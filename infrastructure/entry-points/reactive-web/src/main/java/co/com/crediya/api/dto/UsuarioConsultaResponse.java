package co.com.crediya.api.dto;

import co.com.crediya.model.UsuarioCompleto;
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
@Schema(description = "Respuesta de consulta de usuario")
public class UsuarioConsultaResponse {

    @Schema(description = "Código de estado HTTP", example = "200")
    private String status;
    
    @Schema(description = "Mensaje descriptivo de la operación", example = "Usuario encontrado")
    private String mensaje;
    
    @Schema(description = "Datos del usuario encontrado")
    private UsuarioData usuario;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Datos completos del usuario")
    public static class UsuarioData {
        @Schema(description = "ID único del usuario", example = "1")
        private Long id;
        
        @Schema(description = "Nombres del usuario", example = "Juan Carlos")
        private String nombres;
        
        @Schema(description = "Apellidos del usuario", example = "Pérez González")
        private String apellidos;
        
        @Schema(description = "Correo electrónico del usuario", example = "juan.perez@email.com")
        private String correoElectronico;
        
        @Schema(description = "Número de documento de identidad", example = "12345678")
        private String documentoIdentidad;
        
        @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
        private LocalDate fechaNacimiento;
        
        @Schema(description = "Número de teléfono", example = "3001234567")
        private String telefono;
        
        @Schema(description = "ID del rol del usuario", example = "1")
        private Long idRol;
        
        @Schema(description = "Salario base del usuario", example = "3500000")
        private Integer salarioBase;
    }

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