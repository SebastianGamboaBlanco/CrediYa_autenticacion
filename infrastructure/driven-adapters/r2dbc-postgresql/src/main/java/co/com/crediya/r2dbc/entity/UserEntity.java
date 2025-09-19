package co.com.crediya.r2dbc.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Table("Usuario")
public class UserEntity {

    @Id
    @Column("id_usuario")
    private Long id;

    @Column("nombre")
    private String firstName;

    @Column("apellido")
    private String lastName;

    @Column("email")
    private String email;

    @Column("documento_identidad")
    private String documentIdentity;

    @Column("fecha_nacimiento")
    private LocalDate birthDate;

    @Column("telefono")
    private String phone;

    @Column("id_rol")
    private Long roleId;

    @Column("salario_base")
    private Integer baseSalary;
    
    @Column("password")
    private String password;
}
