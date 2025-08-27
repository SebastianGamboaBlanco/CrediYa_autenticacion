package co.com.crediya.model;

import java.time.LocalDate;

public class UsuarioCompleto {
    private final Long id;
    private final String nombres;
    private final String apellidos;
    private final String correoElectronico;
    private final String documentoIdentidad;
    private final LocalDate fechaNacimiento;
    private final String telefono;
    private final Long idRol;
    private final Integer salarioBase;

    public UsuarioCompleto(Long id, String nombres, String apellidos, String correoElectronico, 
                          String documentoIdentidad, LocalDate fechaNacimiento, String telefono, 
                          Long idRol, Integer salarioBase) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.documentoIdentidad = documentoIdentidad;
        this.fechaNacimiento = fechaNacimiento;
        this.telefono = telefono;
        this.idRol = idRol;
        this.salarioBase = salarioBase;
    }

    public Long getId() { return id; }
    public String getNombres() { return nombres; }
    public String getApellidos() { return apellidos; }
    public String getCorreoElectronico() { return correoElectronico; }
    public String getDocumentoIdentidad() { return documentoIdentidad; }
    public LocalDate getFechaNacimiento() { return fechaNacimiento; }
    public String getTelefono() { return telefono; }
    public Long getIdRol() { return idRol; }
    public Integer getSalarioBase() { return salarioBase; }
}