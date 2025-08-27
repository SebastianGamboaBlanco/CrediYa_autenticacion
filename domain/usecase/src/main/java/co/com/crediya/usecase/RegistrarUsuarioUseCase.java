package co.com.crediya.usecase;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.exceptions.CorreoExistInvalidException;
import co.com.crediya.model.exceptions.DocumentoExistInvalidException;
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
                                        return Mono.error(new CorreoExistInvalidException(correoElectronico));
                                    }
                                    return usuarioRepository.existeDocumentoIdentidad(documentoIdentidad)
                                            .flatMap(existeDocumento -> {
                                                if (existeDocumento) {
                                                    return Mono.error(new DocumentoExistInvalidException(documentoIdentidad));
                                                }
                                                return usuarioRepository.registrarUsuario(usuario, documentoIdentidad, 
                                                                                        fechaNacimiento, telefono, idRol);
                                            });
                                })
                );
    }
}
