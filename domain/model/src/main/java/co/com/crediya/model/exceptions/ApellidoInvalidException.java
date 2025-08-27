package co.com.crediya.model.exceptions;

public class ApellidoInvalidException extends RuntimeException{
    public ApellidoInvalidException(){
        super("Apellido es obligatorio");
    }
}
