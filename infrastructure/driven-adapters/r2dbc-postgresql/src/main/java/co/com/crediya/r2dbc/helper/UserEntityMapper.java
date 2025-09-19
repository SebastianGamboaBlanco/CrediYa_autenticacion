package co.com.crediya.r2dbc.helper;

import co.com.crediya.model.User;
import co.com.crediya.model.CompleteUser;
import co.com.crediya.model.valueobjects.Role;
import co.com.crediya.r2dbc.entity.UserEntity;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Component
public class UserEntityMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MM-dd-yyyy");

    public UserEntity toEntity(User user, String documentIdentity, String birthDate, 
                              String phone, Long roleId, String password){
        LocalDate date = birthDate != null && !birthDate.trim().isEmpty() 
            ? LocalDate.parse(birthDate.trim(), FORMATTER) 
            : null;

        return UserEntity.builder()
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .documentIdentity(documentIdentity)
                .birthDate(date)
                .phone(phone)
                .roleId(roleId)
                .baseSalary(user.getBaseSalary())
                .password(password)
                .build();
    }

    public User toDomain(UserEntity userEntity){
        return new User(userEntity.getFirstName(),
                       userEntity.getLastName(),
                       userEntity.getEmail(),
                       userEntity.getBaseSalary());
    }

    public CompleteUser toCompleteUser(UserEntity userEntity) {
        return new CompleteUser(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getDocumentIdentity(),
                userEntity.getBirthDate(),
                userEntity.getPhone(),
                userEntity.getRoleId(),
                userEntity.getBaseSalary()
        );
    }

    public CompleteUser toCompleteUserWithRole(UserEntity userEntity, Role role) {
        CompleteUser user = new CompleteUser(
                userEntity.getId(),
                userEntity.getFirstName(),
                userEntity.getLastName(),
                userEntity.getEmail(),
                userEntity.getDocumentIdentity(),
                userEntity.getBirthDate(),
                userEntity.getPhone(),
                userEntity.getRoleId(),
                userEntity.getBaseSalary()
        );
        user.setRole(role);
        return user;
    }
}
