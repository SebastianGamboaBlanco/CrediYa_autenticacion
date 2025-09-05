package co.com.crediya.model.gateways;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.UsuarioCompleto;
import reactor.core.publisher.Mono;

public interface UsuarioRepository {
    Mono<Void> registrarUsuario(Usuario usuario, String documentoIdentidad, String fechaNacimiento, 
                               String telefono, Long idRol, String password);

    Mono<Boolean> existeEmail(String email);

    Mono<Boolean> existeDocumentoIdentidad(String documentoIdentidad);

    Mono<UsuarioCompleto> buscarPorDocumentoIdentidad(String documentoIdentidad);

}
