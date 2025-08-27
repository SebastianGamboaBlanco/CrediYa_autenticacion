package co.com.crediya.model.exceptions;

public class DocumentoFormatoInvalidException extends RuntimeException {
    public DocumentoFormatoInvalidException(String documentoIdentidad) {
        super("Documento de identidad inválido: " + documentoIdentidad + 
              ". Debe contener entre 4 y 20 dígitos numéricos.");
    }
}