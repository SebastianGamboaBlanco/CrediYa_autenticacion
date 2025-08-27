package co.com.crediya.model.exceptions;

public class RolInvalidException extends RuntimeException {
    public RolInvalidException(Long idRol) {
        super("El rol con ID " + idRol + " no existe o no es válido");
    }
}