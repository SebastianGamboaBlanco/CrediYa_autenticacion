package co.com.crediya.model.valueobjects;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Objects;

public class FechaNacimiento {
    private final LocalDate fecha;

    private FechaNacimiento(LocalDate fecha) {
        this.fecha = fecha;
    }

    public static FechaNacimiento of(String fechaString) {
        try {
            LocalDate fecha = LocalDate.parse(fechaString, DateTimeFormatter.ISO_LOCAL_DATE);
            return new FechaNacimiento(fecha);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido. Use formato YYYY-MM-DD", e);
        }
    }

    public static FechaNacimiento of(LocalDate fecha) {
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha de nacimiento no puede ser null");
        }
        return new FechaNacimiento(fecha);
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getFechaString() {
        return fecha.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FechaNacimiento that = (FechaNacimiento) o;
        return Objects.equals(fecha, that.fecha);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fecha);
    }

    @Override
    public String toString() {
        return getFechaString();
    }
}