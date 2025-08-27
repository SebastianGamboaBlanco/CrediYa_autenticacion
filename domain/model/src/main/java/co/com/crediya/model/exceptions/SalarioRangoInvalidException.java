package co.com.crediya.model.exceptions;

public class SalarioRangoInvalidException extends RuntimeException{
    private static final int SALARIO_MINIMO = 0;
    private static final int SALARIO_MAXIMO = 15000000;
    
    public SalarioRangoInvalidException(){
        super(String.format("Valor de salario base inválido debe estar entre %d y %d", 
                           SALARIO_MINIMO, SALARIO_MAXIMO));
    }
}
