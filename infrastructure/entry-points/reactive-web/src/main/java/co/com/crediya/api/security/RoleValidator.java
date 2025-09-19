package co.com.crediya.api.security;

import co.com.crediya.model.valueobjects.Role;
import co.com.crediya.model.exceptions.InsufficientPermissionsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Set;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RoleValidator {


    private static final Set<Long> REGISTRATION_ALLOWED_ROLE_IDS = Set.of(1L, 2L);
    

    public static Mono<Void> requirePermissions(ServerWebExchange exchange) {
        return Mono.fromCallable(() -> {
            Role userRole = (Role) exchange.getAttribute("user.role");
            
            if (userRole == null) {
                log.warn("Role information not found in context for user registration");
                return false;
            }
            
            boolean hasPermission = REGISTRATION_ALLOWED_ROLE_IDS.contains(userRole.getId());
            log.debug("Registration permission validation - User: {}, Role ID: {}, Role: {}, Allowed: {}", 
                     exchange.getAttribute("user.id"), userRole.getId(), userRole.getName(), hasPermission);
            
            return hasPermission;
        })
        .flatMap(hasPermission -> {
            if (!hasPermission) {
                String userId = (String) exchange.getAttribute("user.id");
                Role userRole = (Role) exchange.getAttribute("user.role");
                String currentRole = userRole != null ? userRole.getName() : "UNKNOWN";
                
                log.warn("Access denied for registration - User ID: {}, Current role: {}", userId, currentRole);
                return Mono.error(InsufficientPermissionsException.registrationPermissionRequired(userId, currentRole));
            }
            return Mono.empty();
        });
    }
}