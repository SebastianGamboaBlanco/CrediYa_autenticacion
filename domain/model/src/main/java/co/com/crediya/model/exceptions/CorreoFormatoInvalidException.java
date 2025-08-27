package co.com.crediya.model.exceptions;

public class CorreoFormatoInvalidException extends RuntimeException{
    public CorreoFormatoInvalidException(String email) {
        super("El correo no tiene un formato válido: " + email);
    }
}
