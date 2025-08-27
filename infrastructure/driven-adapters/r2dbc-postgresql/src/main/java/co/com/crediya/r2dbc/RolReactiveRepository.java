package co.com.crediya.r2dbc;

import co.com.crediya.r2dbc.entity.RolEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface RolReactiveRepository extends ReactiveCrudRepository<RolEntity, Long> {
    Mono<Boolean> existsById(Long id);
}