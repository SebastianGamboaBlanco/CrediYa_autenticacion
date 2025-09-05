package co.com.crediya.usecase;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import co.com.crediya.model.gateways.PasswordService;
import co.com.crediya.model.gateways.UsuarioRepository;
import reactor.core.publisher.Mono;

public class RegistrarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;
    private final PasswordService passwordService;

    public RegistrarUsuarioUseCase(UsuarioRepository usuarioRepository, PasswordService passwordService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordService = passwordService;
    }

    public Mono<Void> registrarUsuario(String nombres, String apellidos, String correoElectronico, Integer salarioBase,
                                       String documentoIdentidad, String fechaNacimiento, String telefono, Long idRol) {
        return Mono.fromCallable(() -> new Usuario(nombres, apellidos, correoElectronico, salarioBase))
                .flatMap(usuario -> validarUsuarioExistente(usuario, documentoIdentidad)
                        .then(generarCredencialesEncriptadas())
                        .flatMap(credenciales -> persistirUsuario(usuario, documentoIdentidad, fechaNacimiento, telefono, idRol, credenciales)));
    }

    private Mono<Void> validarUsuarioExistente(Usuario usuario, String documentoIdentidad) {
        return validarEmailUnico(usuario.getCorreoElectronico())
                .then(validarDocumentoUnico(documentoIdentidad));
    }

    private Mono<Void> validarEmailUnico(String correoElectronico) {
        return usuarioRepository.existeEmail(correoElectronico)
                .flatMap(existeEmail -> {
                    if (existeEmail) {
                        return Mono.error(new BusinessException(ErrorCode.CORREO_ALREADY_EXISTS, correoElectronico));
                    }
                    return Mono.empty();
                });
    }

    private Mono<Void> validarDocumentoUnico(String documentoIdentidad) {
        return usuarioRepository.existeDocumentoIdentidad(documentoIdentidad)
                .flatMap(existeDocumento -> {
                    if (existeDocumento) {
                        return Mono.error(new BusinessException(ErrorCode.DOCUMENTO_ALREADY_EXISTS, documentoIdentidad));
                    }
                    return Mono.empty();
                });
    }

    private Mono<String> generarCredencialesEncriptadas() {
        return Mono.fromCallable(() -> {
            String randomPassword = passwordService.generateRandomPassword();
            return passwordService.encryptPassword(randomPassword);
        });
    }

    private Mono<Void> persistirUsuario(Usuario usuario, String documentoIdentidad, String fechaNacimiento, 
                                       String telefono, Long idRol, String passwordEncriptada) {
        return usuarioRepository.registrarUsuario(usuario, documentoIdentidad, fechaNacimiento, telefono, idRol, passwordEncriptada);
    }
}
