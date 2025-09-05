package co.com.crediya.r2dbc.helper;

import co.com.crediya.model.valueobjects.Role;
import co.com.crediya.r2dbc.entity.RolEntity;
import org.springframework.stereotype.Component;

@Component
public class RolEntityMapper {

    public Role toDomain(RolEntity rolEntity) {
        if (rolEntity == null) {
            return null;
        }
        
        return Role.of(
            rolEntity.getId(),
            rolEntity.getNombre()
        );
    }

    public RolEntity toEntity(Role role) {
        if (role == null) {
            return null;
        }
        
        return RolEntity.builder()
                .id(role.getId())
                .nombre(role.getName())
                .build();
    }
}