package co.com.crediya.model.exceptions;

public class DocumentoExistInvalidException extends RuntimeException {
    public DocumentoExistInvalidException(String documentoIdentidad) {
        super("El documento de identidad " + documentoIdentidad + " ya se encuentra registrado");
    }
}