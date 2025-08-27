package co.com.crediya.r2dbc.helper;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.r2dbc.entity.UsuarioEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UsuarioEntityMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public UsuarioEntity toEntity(Usuario usuario, String documentoIdentidad, String fechaNacimiento, 
                                   String telefono, Long idRol){
        LocalDate fecha = fechaNacimiento != null && !fechaNacimiento.trim().isEmpty() 
            ? LocalDate.parse(fechaNacimiento.trim(), FORMATTER) 
            : null;

        return UsuarioEntity.builder()
                .nombres(usuario.getNombres())
                .apellidos(usuario.getApellidos())
                .correo(usuario.getCorreoElectronico())
                .documentoIdentidad(documentoIdentidad)
                .fechaNacimiento(fecha)
                .telefono(telefono)
                .idRol(idRol)
                .salarioBase(usuario.getSalarioBase())
                .build();
    }

    public Usuario toDomain(UsuarioEntity usuarioEntity){
        return new Usuario(usuarioEntity.getNombres(),
                           usuarioEntity.getApellidos(),
                           usuarioEntity.getCorreo(),
                           usuarioEntity.getSalarioBase());
    }

    public UsuarioCompleto toUsuarioCompleto(UsuarioEntity usuarioEntity) {
        return new UsuarioCompleto(
                usuarioEntity.getId(),
                usuarioEntity.getNombres(),
                usuarioEntity.getApellidos(),
                usuarioEntity.getCorreo(),
                usuarioEntity.getDocumentoIdentidad(),
                usuarioEntity.getFechaNacimiento(),
                usuarioEntity.getTelefono(),
                usuarioEntity.getIdRol(),
                usuarioEntity.getSalarioBase()
        );
    }
}
