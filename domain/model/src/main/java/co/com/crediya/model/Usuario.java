package co.com.crediya.model;

import co.com.crediya.model.valueobjects.Email;
import co.com.crediya.model.valueobjects.NombreCompleto;
import co.com.crediya.model.valueobjects.SalarioBase;

public class Usuario {
    private final NombreCompleto nombreCompleto;
    private final Email correoElectronico;
    private final SalarioBase salarioBase;

    private Usuario(Builder builder) {
        this.nombreCompleto = builder.nombreCompleto;
        this.correoElectronico = builder.correoElectronico;
        this.salarioBase = builder.salarioBase;
    }

    public Usuario(String nombres, String apellidos, String correoElectronico, Integer salarioBase) {
        this.nombreCompleto = NombreCompleto.of(nombres, apellidos);
        this.correoElectronico = Email.of(correoElectronico);
        this.salarioBase = SalarioBase.of(salarioBase);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getNombres() {
        return nombreCompleto.getNombres();
    }

    public String getApellidos() {
        return nombreCompleto.getApellidos();
    }

    public String getCorreoElectronico() {
        return correoElectronico.getValor();
    }

    public Integer getSalarioBase() {
        return salarioBase.getValor();
    }

    public NombreCompleto getNombreCompleto() {
        return nombreCompleto;
    }

    public Email getEmail() {
        return correoElectronico;
    }

    public SalarioBase getSalario() {
        return salarioBase;
    }

    public static class Builder {
        private NombreCompleto nombreCompleto;
        private Email correoElectronico;
        private SalarioBase salarioBase;

        private Builder() {}

        public Builder conNombreCompleto(String nombres, String apellidos) {
            this.nombreCompleto = NombreCompleto.of(nombres, apellidos);
            return this;
        }

        public Builder conNombreCompleto(NombreCompleto nombreCompleto) {
            this.nombreCompleto = nombreCompleto;
            return this;
        }

        public Builder conEmail(String email) {
            this.correoElectronico = Email.of(email);
            return this;
        }

        public Builder conEmail(Email email) {
            this.correoElectronico = email;
            return this;
        }

        public Builder conSalarioBase(Integer salario) {
            this.salarioBase = SalarioBase.of(salario);
            return this;
        }

        public Builder conSalarioBase(SalarioBase salarioBase) {
            this.salarioBase = salarioBase;
            return this;
        }

        public Usuario build() {
            if (nombreCompleto == null || correoElectronico == null || salarioBase == null) {
                throw new IllegalArgumentException("Todos los campos son requeridos para construir un Usuario");
            }
            return new Usuario(this);
        }
    }
}
