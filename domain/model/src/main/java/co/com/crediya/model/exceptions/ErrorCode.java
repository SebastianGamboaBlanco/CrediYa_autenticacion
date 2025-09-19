package co.com.crediya.model.exceptions;

public enum ErrorCode {

    FIRST_NAME_REQUIRED("firstName", ErrorType.VALIDATION, "First name is required and cannot be empty"),
    LAST_NAME_REQUIRED("lastName", ErrorType.VALIDATION, "Last name is required and cannot be empty"),
    EMAIL_REQUIRED("email", ErrorType.VALIDATION, "Email is required"),
    SALARY_REQUIRED("baseSalary", ErrorType.VALIDATION, "Base salary is required"),
    ROLE_INVALID("roleId", ErrorType.VALIDATION, "The specified role is not valid"),
    

    EMAIL_FORMAT_INVALID("email", ErrorType.FORMAT, "Email format '%s' is not valid"),
    DOCUMENT_FORMAT_INVALID("documentIdentity", ErrorType.FORMAT, "Document '%s' must contain between 4 and 20 digits"),
    

    EMAIL_ALREADY_EXISTS("email", ErrorType.ALREADY_EXISTS, "A user is already registered with email '%s'"),
    DOCUMENT_ALREADY_EXISTS("documentIdentity", ErrorType.ALREADY_EXISTS, "A user is already registered with document '%s'"),
    

    SALARY_OUT_OF_RANGE("baseSalary", ErrorType.OUT_OF_RANGE, "Salary must be between %d and %d"),

    EMAIL_NOT_EXISTS("email", ErrorType.VALIDATION, "Email does not exist"),
    INVALID_PASSWORD("password", ErrorType.VALIDATION, "Invalid password");
    
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