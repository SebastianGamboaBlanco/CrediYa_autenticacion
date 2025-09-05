package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class NombreCompleto {
    private final String nombres;
    private final String apellidos;

    private NombreCompleto(String nombres, String apellidos) {
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public static NombreCompleto of(String nombres, String apellidos) {
        validarNombres(nombres);
        validarApellidos(apellidos);
        return new NombreCompleto(nombres, apellidos);
    }

    private static void validarNombres(String nombres) {
        if (nombres == null || nombres.isBlank()) {
            throw new BusinessException(ErrorCode.NOMBRE_REQUIRED, nombres);
        }
    }

    private static void validarApellidos(String apellidos) {
        if (apellidos == null || apellidos.isBlank()) {
            throw new BusinessException(ErrorCode.APELLIDO_REQUIRED, apellidos);
        }
    }

    public String getNombres() {
        return nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NombreCompleto that = (NombreCompleto) o;
        return Objects.equals(nombres, that.nombres) && Objects.equals(apellidos, that.apellidos);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nombres, apellidos);
    }

    @Override
    public String toString() {
        return getNombreCompleto();
    }
}