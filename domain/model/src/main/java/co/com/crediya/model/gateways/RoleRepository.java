package co.com.crediya.model.gateways;

import co.com.crediya.model.valueobjects.Role;
import reactor.core.publisher.Mono;

public interface RoleRepository {
    Mono<Boolean> roleExists(Long roleId);
    Mono<Role> findRoleById(Long roleId);
}