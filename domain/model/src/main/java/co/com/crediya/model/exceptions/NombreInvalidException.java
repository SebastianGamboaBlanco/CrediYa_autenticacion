package co.com.crediya.model.exceptions;

public class NombreInvalidException extends RuntimeException{
    public NombreInvalidException(){
        super("El nombre es obligatorio");
    }
}
