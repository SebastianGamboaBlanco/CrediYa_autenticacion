package co.com.crediya.model.gateways;

import co.com.crediya.model.valueobjects.Role;
import reactor.core.publisher.Mono;

public interface RolRepository {
    Mono<Boolean> existeRol(Long idRol);
    Mono<Role> findRoleById(Long idRol);
}