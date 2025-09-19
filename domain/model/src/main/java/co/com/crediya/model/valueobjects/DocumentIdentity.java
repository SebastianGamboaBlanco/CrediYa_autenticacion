package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class DocumentIdentity {
    private final String value;

    private DocumentIdentity(String value) {
        this.value = value;
    }

    public static DocumentIdentity of(String document) {
        validate(document);
        return new DocumentIdentity(document);
    }

    private static void validate(String document) {
        if (document == null || document.isBlank()) {
            throw new BusinessException(ErrorCode.DOCUMENT_FORMAT_INVALID, document);
        }
        
        if (document.length() < 4 || document.length() > 20 || !document.matches("\\d+")) {
            throw new BusinessException(ErrorCode.DOCUMENT_FORMAT_INVALID, document);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DocumentIdentity that = (DocumentIdentity) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}