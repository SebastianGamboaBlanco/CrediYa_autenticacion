package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class DocumentoIdentidad {
    private final String valor;

    private DocumentoIdentidad(String valor) {
        this.valor = valor;
    }

    public static DocumentoIdentidad of(String documento) {
        validar(documento);
        return new DocumentoIdentidad(documento);
    }

    private static void validar(String documento) {
        if (documento == null || documento.isBlank()) {
            throw new BusinessException(ErrorCode.DOCUMENTO_FORMAT_INVALID, documento);
        }
        
        if (documento.length() < 4 || documento.length() > 20 || !documento.matches("\\d+")) {
            throw new BusinessException(ErrorCode.DOCUMENTO_FORMAT_INVALID, documento);
        }
    }

    public String getValor() {
        return valor;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentoIdentidad that = (DocumentoIdentidad) o;
        return Objects.equals(valor, that.valor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(valor);
    }

    @Override
    public String toString() {
        return valor;
    }
}