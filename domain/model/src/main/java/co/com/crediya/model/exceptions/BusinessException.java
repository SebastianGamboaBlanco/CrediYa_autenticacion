package co.com.crediya.model.exceptions;

public class BusinessException extends RuntimeException {
    private final ErrorCode errorCode;
    private final String value;

    public BusinessException(ErrorCode errorCode, Object... args) {
        super(errorCode.formatMessage(args));
        this.errorCode = errorCode;
        this.value = args.length > 0 ? String.valueOf(args[0]) : null;
    }


    public String getField() {
        return errorCode.getField();
    }

    public String getValue() {
        return value;
    }

    public ErrorType getErrorType() {
        return errorCode.getType();
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}