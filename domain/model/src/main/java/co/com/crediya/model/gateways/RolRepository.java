package co.com.crediya.model.gateways;

import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Boolean> existeRol(Long idRol);
}