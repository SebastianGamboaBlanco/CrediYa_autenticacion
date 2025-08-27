package co.com.crediya.model.exceptions;

public class CorreoExistInvalidException extends RuntimeException {
    public CorreoExistInvalidException(String correo){
        super("El email ya está registrado: " + correo);
    }
}
