package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.UsuarioEntity;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface UsuarioReactiveRepository extends ReactiveCrudRepository<UsuarioEntity, Long>, ReactiveQueryByExampleExecutor<UsuarioEntity> {
    Mono<Boolean> existsByCorreo(String correo);

    Mono<Boolean> existsByDocumentoIdentidad(String documentoIdentidad);

    Mono<UsuarioEntity> findByDocumentoIdentidad(String documentoIdentidad);

    Mono<UsuarioEntity> findByCorreo(String correo);

}
