package co.com.crediya.model;

import co.com.crediya.model.exceptions.*;
import java.util.regex.Pattern;

public class Usuario {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_%+-]+([.][a-zA-Z0-9_%+-]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    private static final int SALARIO_MINIMO = 0;
    private static final int SALARIO_MAXIMO = 15000000;
    
    private final String nombres;
    private final String apellidos;
    private final String correoElectronico;
    private final Integer salarioBase;

    public Usuario(String nombres, String apellidos, String correoElectronico, Integer salarioBase) {
        validarNombre(nombres);
        validarApellidos(apellidos);
        validarCorreo(correoElectronico);
        validarSalarioBase(salarioBase);
        
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.correoElectronico = correoElectronico;
        this.salarioBase = salarioBase;
    }

    private void validarNombre(String nombre){
        if(nombre == null || nombre.isBlank()) {
            throw new NombreInvalidException();
        }
    }
    private void validarApellidos(String apellidos){
        if(apellidos == null || apellidos.isBlank()){
            throw new ApellidoInvalidException();
        }
    }
    private void validarCorreo(String correo){
        if(correo == null || correo.isBlank()) {
            throw new CorreoInvalidException();
        }
        try {
            if(!EMAIL_PATTERN.matcher(correo).matches()) {
                throw new CorreoFormatoInvalidException(correo);
            }
        } catch (Exception e) {
            throw new CorreoFormatoInvalidException(correo);
        }
    }

    private void validarSalarioBase(Integer salarioBase){
        if(salarioBase == null ){
            throw new SalarioInvalidException();
        }

        if(salarioBase < SALARIO_MINIMO || salarioBase > SALARIO_MAXIMO) {
            throw new SalarioRangoInvalidException();
        }
    }


    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }


    public Integer getSalarioBase() {
        return salarioBase;
    }
}
