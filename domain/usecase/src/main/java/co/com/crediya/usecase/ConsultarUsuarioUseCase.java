package co.com.crediya.usecase;

import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.gateways.UsuarioRepository;
import reactor.core.publisher.Mono;

public class ConsultarUsuarioUseCase {
    private final UsuarioRepository usuarioRepository;

    public ConsultarUsuarioUseCase(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public Mono<UsuarioCompleto> consultarPorDocumentoIdentidad(String documentoIdentidad) {
        return usuarioRepository.buscarPorDocumentoIdentidad(documentoIdentidad);
    }
}