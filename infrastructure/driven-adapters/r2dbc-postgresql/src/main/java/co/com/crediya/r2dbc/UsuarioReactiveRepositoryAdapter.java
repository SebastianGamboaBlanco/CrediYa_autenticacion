package co.com.crediya.r2dbc;

import co.com.crediya.model.Usuario;
import co.com.crediya.model.UsuarioCompleto;
import co.com.crediya.model.gateways.UsuarioRepository;
import co.com.crediya.r2dbc.entity.UsuarioEntity;
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
                                      String telefono, Long idRol) {
        log.info("Iniciando guardado en BD - Email: {}, DocumentoID: {}", usuario.getCorreoElectronico(), documentoIdentidad);
        
        UsuarioEntity entity = mapper.toEntity(usuario, documentoIdentidad, fechaNacimiento, telefono, idRol);
        log.debug("Entity mapeada - Email: {}, Salario: {}", entity.getCorreo(), entity.getSalarioBase());
        
        return repository.save(entity)
                .doOnNext(savedEntity -> log.info("Usuario guardado exitosamente - ID: {}, Email: {}", 
                        savedEntity.getId(), savedEntity.getCorreo()))
                .doOnError(error -> log.error("Error guardando usuario en BD - Email: {}, DocumentoID: {}, Error: {}", 
                        usuario.getCorreoElectronico(), documentoIdentidad, error.getMessage(), error))
                .then();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existeEmail(String email) {
        log.debug("Verificando existencia de email en BD: {}", email);
        
        return repository.existsByCorreo(email)
                .doOnNext(existe -> log.debug("Resultado consulta existencia email - Email: {}, Existe: {}", email, existe))
                .doOnError(error -> log.error("Error verificando existencia de email - Email: {}, Error: {}", 
                        email, error.getMessage(), error));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> existeDocumentoIdentidad(String documentoIdentidad) {
        log.debug("Verificando existencia de documento de identidad en BD: {}", documentoIdentidad);
        
        return repository.existsByDocumentoIdentidad(documentoIdentidad)
                .doOnNext(existe -> log.debug("Resultado consulta existencia documento - DocumentoID: {}, Existe: {}", documentoIdentidad, existe))
                .doOnError(error -> log.error("Error verificando existencia de documento - DocumentoID: {}, Error: {}", 
                        documentoIdentidad, error.getMessage(), error));
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UsuarioCompleto> buscarPorDocumentoIdentidad(String documentoIdentidad) {
        log.info("Buscando usuario completo por documento de identidad en BD: {}", documentoIdentidad);
        
        return repository.findByDocumentoIdentidad(documentoIdentidad)
                .doOnNext(entity -> log.info("Usuario encontrado - DocumentoID: {}, Email: {}, ID: {}", 
                        documentoIdentidad, entity.getCorreo(), entity.getId()))
                .map(mapper::toUsuarioCompleto)
                .doOnNext(usuario -> log.debug("Usuario mapeado correctamente - DocumentoID: {}", documentoIdentidad))
                .doOnError(error -> log.error("Error buscando usuario por documento - DocumentoID: {}, Error: {}", 
                        documentoIdentidad, error.getMessage(), error));
    }
}