package co.com.crediya.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import co.com.crediya.model.valueobjects.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleInfo {
    
    @JsonProperty("id")
    private Long id;
    
    @JsonProperty("name")
    private String name;
    
    public static RoleInfo from(Role role) {
        return RoleInfo.builder()
                .id(role.getId())
                .name(role.getName())
                .build();
    }
}