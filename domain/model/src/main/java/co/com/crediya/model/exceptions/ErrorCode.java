package co.com.crediya.model.exceptions;

public enum ErrorCode {

    NOMBRE_REQUIRED("nombres", ErrorType.VALIDATION, "El nombre es requerido y no puede estar vacío"),
    APELLIDO_REQUIRED("apellidos", ErrorType.VALIDATION, "Los apellidos son requeridos y no pueden estar vacíos"),
    CORREO_REQUIRED("correoElectronico", ErrorType.VALIDATION, "El correo electrónico es requerido"),
    SALARIO_REQUIRED("salarioBase", ErrorType.VALIDATION, "El salario base es requerido"),
    ROL_INVALID("idRol", ErrorType.VALIDATION, "El rol especificado no es válido"),
    

    CORREO_FORMAT_INVALID("correoElectronico", ErrorType.FORMAT, "El formato del correo '%s' no es válido"),
    DOCUMENTO_FORMAT_INVALID("documentoIdentidad", ErrorType.FORMAT, "El documento '%s' debe contener entre 4 y 20 dígitos"),
    

    CORREO_ALREADY_EXISTS("correoElectronico", ErrorType.ALREADY_EXISTS, "Ya existe un usuario registrado con el correo '%s'"),
    DOCUMENTO_ALREADY_EXISTS("documentoIdentidad", ErrorType.ALREADY_EXISTS, "Ya existe un usuario registrado con el documento '%s'"),
    

    SALARIO_OUT_OF_RANGE("salarioBase", ErrorType.OUT_OF_RANGE, "El salario debe estar entre %d y %d");
    
    private final String field;
    private final ErrorType type;
    private final String messageTemplate;
    
    ErrorCode(String field, ErrorType type, String messageTemplate) {
        this.field = field;
        this.type = type;
        this.messageTemplate = messageTemplate;
    }
    
    public String getField() {
        return field;
    }
    
    public ErrorType getType() {
        return type;
    }
    
    
    public String formatMessage(Object... args) {
        return String.format(messageTemplate, args);
    }
}