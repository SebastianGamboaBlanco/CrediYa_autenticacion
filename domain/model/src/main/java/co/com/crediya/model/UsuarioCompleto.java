package co.com.crediya.model;

import co.com.crediya.model.valueobjects.*;
import java.time.LocalDate;

public class UsuarioCompleto {
    private final Long id;
    private final NombreCompleto nombreCompleto;
    private final Email correoElectronico;
    private final DocumentoIdentidad documentoIdentidad;
    private final FechaNacimiento fechaNacimiento;
    private final Telefono telefono;
    private final IdRol idRol;
    private final SalarioBase salarioBase;

    public UsuarioCompleto(Long id, String nombres, String apellidos, String correoElectronico, 
                          String documentoIdentidad, LocalDate fechaNacimiento, String telefono, 
                          Long idRol, Integer salarioBase) {
        this.id = id;
        this.nombreCompleto = NombreCompleto.of(nombres, apellidos);
        this.correoElectronico = Email.of(correoElectronico);
        this.documentoIdentidad = DocumentoIdentidad.of(documentoIdentidad);
        this.fechaNacimiento = FechaNacimiento.of(fechaNacimiento);
        this.telefono = Telefono.of(telefono);
        this.idRol = IdRol.of(idRol);
        this.salarioBase = SalarioBase.of(salarioBase);
        this.role = null;
    }

    public Long getId() { return id; }
    public String getNombres() { return nombreCompleto.getNombres(); }
    public String getApellidos() { return nombreCompleto.getApellidos(); }
    public String getCorreoElectronico() { return correoElectronico.getValor(); }
    public String getDocumentoIdentidad() { return documentoIdentidad.getValor(); }
    public LocalDate getFechaNacimiento() { return fechaNacimiento.getFecha(); }
    public String getTelefono() { return telefono.getNumero(); }
    public Long getIdRol() { return idRol.getValor(); }
    public Integer getSalarioBase() { return salarioBase.getValor(); }

    public NombreCompleto getNombreCompleto() { return nombreCompleto; }
    public Email getEmail() { return correoElectronico; }
    public DocumentoIdentidad getDocumento() { return documentoIdentidad; }
    public FechaNacimiento getFecha() { return fechaNacimiento; }
    public Telefono getTelefonoCompleto() { return telefono; }
    public IdRol getRol() { return idRol; }
    public SalarioBase getSalario() { return salarioBase; }
    
    private Role role;
    
    public Role getRole() { 
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
}