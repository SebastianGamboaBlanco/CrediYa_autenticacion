package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class SalarioBase {
    private static final int SALARIO_MINIMO = 0;
    private static final int SALARIO_MAXIMO = 15000000;
    
    private final Integer valor;

    private SalarioBase(Integer valor) {
        this.valor = valor;
    }

    public static SalarioBase of(Integer salario) {
        validar(salario);
        return new SalarioBase(salario);
    }

    private static void validar(Integer salario) {
        if (salario == null) {
            throw new BusinessException(ErrorCode.SALARIO_REQUIRED, "null");
        }

        if (salario < SALARIO_MINIMO || salario > SALARIO_MAXIMO) {
            throw new BusinessException(ErrorCode.SALARIO_OUT_OF_RANGE, salario, SALARIO_MINIMO, SALARIO_MAXIMO);
        }
    }

    public Integer getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SalarioBase that = (SalarioBase) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return String.valueOf(valor);
    }
}