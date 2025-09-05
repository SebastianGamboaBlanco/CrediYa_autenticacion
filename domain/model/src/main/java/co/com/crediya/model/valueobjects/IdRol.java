package co.com.crediya.model.valueobjects;

import java.util.Objects;

public class IdRol {
    private final Long valor;

    private IdRol(Long valor) {
        this.valor = valor;
    }

    public static IdRol of(Long idRol) {
        if (idRol == null || idRol <= 0) {
            throw new IllegalArgumentException("El ID del rol debe ser un número positivo");
        }
        return new IdRol(idRol);
    }

    public Long getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IdRol idRol = (IdRol) o;
        return Objects.equals(valor, idRol.valor);
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