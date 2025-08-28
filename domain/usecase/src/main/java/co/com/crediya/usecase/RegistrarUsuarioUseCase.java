package co.com.crediya.usecase;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.UsuarioRepository;
import reactor.core.publisher.Mono;

public class RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;

    public RegistrarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Mono<Void> registrarUsuario(String nombres, String apellidos, String correoElectronico, Integer salarioBase,
                                       String documentoIdentidad, String fechaNacimiento, String telefono, Long idRol) {
        return Mono.fromCallable(() -> {
                    return new Usuario(nombres, apellidos, correoElectronico, salarioBase);
                })
                .flatMap(usuario ->
                        usuarioRepository.existeEmail(usuario.getCorreoElectronico())
                                .flatMap(existeEmail -> {
                                    if (existeEmail) {
                                        return Mono.error(new BusinessException(ErrorCode.CORREO_ALREADY_EXISTS, correoElectronico));
                                    }
                                    return usuarioRepository.existeDocumentoIdentidad(documentoIdentidad)
                                            .flatMap(existeDocumento -> {
                                                if (existeDocumento) {
                                                    return Mono.error(new BusinessException(ErrorCode.DOCUMENTO_ALREADY_EXISTS, documentoIdentidad));
                                                }
                                                return usuarioRepository.registrarUsuario(usuario, documentoIdentidad, 
                                                                                        fechaNacimiento, telefono, idRol);
                                            });
                                })
                );
    }
}
