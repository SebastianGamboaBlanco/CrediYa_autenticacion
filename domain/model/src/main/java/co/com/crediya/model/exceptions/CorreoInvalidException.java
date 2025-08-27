package co.com.crediya.model.exceptions;

public class CorreoInvalidException extends RuntimeException{
    public CorreoInvalidException(){
        super("Correo electronico es obligatorio");
    }
}
