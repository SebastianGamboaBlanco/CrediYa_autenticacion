package co.com.crediya.r2dbc;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.helper.UsuarioEntityMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

@Slf4j
@Repository
public class UsuarioReactiveRepositoryAdapter implements UsuarioRepository {

    private final UsuarioReactiveRepository repository;
    private final UsuarioEntityMapper mapper;

    public UsuarioReactiveRepositoryAdapter(UsuarioReactiveRepository repository,
                                            UsuarioEntityMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public Mono<Void> registrarUsuario(Usuario usuario, String documentoIdentidad, String fechaNacimiento, 
                                      String telefono, Long idRol, String password) {
        return repository.save(mapper.toEntity(usuario, documentoIdentidad, fechaNacimiento, telefono, idRol, password))
                .doOnError(error -> log.error("Error guardando usuario - Email: {}, Error: {}", 
                        usuario.getCorreoElectronico(), error.getMessage()))
                .then();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existeEmail(String email) {
        return repository.existsByCorreo(email)
                .doOnError(error -> log.error("Error verificando email - {}: {}", email, error.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existeDocumentoIdentidad(String documentoIdentidad) {
        return repository.existsByDocumentoIdentidad(documentoIdentidad)
                .doOnError(error -> log.error("Error verificando documento - {}: {}", documentoIdentidad, error.getMessage()));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UsuarioCompleto> buscarPorDocumentoIdentidad(String documentoIdentidad) {
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .map(mapper::toUsuarioCompleto)
                .doOnError(error -> log.error("Error buscando usuario - {}: {}", documentoIdentidad, error.getMessage()));
    }
}