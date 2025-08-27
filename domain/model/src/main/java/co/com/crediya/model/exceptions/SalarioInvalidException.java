package co.com.crediya.model.exceptions;

public class SalarioInvalidException extends RuntimeException{
    public SalarioInvalidException(){
        super("Salario base es obligatorio");
    }
}
