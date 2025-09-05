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
public class UsuarioEntity {

    @Id
    @Column("id_usuario")
    private Long id;

    @Column("nombre")
    private String nombres;

    @Column("apellido")
    private String apellidos;

    @Column("email")
    private String correo;

    @Column("documento_identidad")
    private String documentoIdentidad;

    @Column("fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Column("telefono")
    private String telefono;

    @Column("id_rol")
    private Long idRol;

    @Column("salario_base")
    private Integer salarioBase;
    
    @Column("password")
    private String password;
}
