package co.com.crediya.model;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.regex.Pattern;

public class Usuario {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_%+-]+(?:\\.[a-zA-Z0-9_%+-]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
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
            throw new BusinessException(ErrorCode.NOMBRE_REQUIRED, nombre);
        }
    }
    
    private void validarApellidos(String apellidos){
        if(apellidos == null || apellidos.isBlank()){
            throw new BusinessException(ErrorCode.APELLIDO_REQUIRED, apellidos);
        }
    }
    
    private void validarCorreo(String correo){
        if(correo == null || correo.isBlank()) {
            throw new BusinessException(ErrorCode.CORREO_REQUIRED, correo);
        }
        try {
            if(!EMAIL_PATTERN.matcher(correo).matches()) {
                throw new BusinessException(ErrorCode.CORREO_FORMAT_INVALID, correo);
            }
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.CORREO_FORMAT_INVALID, correo);
        }
    }

    private void validarSalarioBase(Integer salarioBase){
        if(salarioBase == null ){
            throw new BusinessException(ErrorCode.SALARIO_REQUIRED, String.valueOf(salarioBase));
        }

        if(salarioBase < SALARIO_MINIMO || salarioBase > SALARIO_MAXIMO) {
            throw new BusinessException(ErrorCode.SALARIO_OUT_OF_RANGE, salarioBase, SALARIO_MINIMO, SALARIO_MAXIMO);
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
