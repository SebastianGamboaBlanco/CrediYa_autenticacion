package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;
import java.util.regex.Pattern;

public class Email {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[a-zA-Z0-9_%+-]+(?:\\.[a-zA-Z0-9_%+-]+)*@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
    
    private final String value;

    private Email(String value) {
        this.value = value;
    }

    public static Email of(String email) {
        validate(email);
        return new Email(email);
    }

    private static void validate(String email) {
        if (email == null || email.isBlank()) {
            throw new BusinessException(ErrorCode.EMAIL_REQUIRED, email);
        }
        
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new BusinessException(ErrorCode.EMAIL_FORMAT_INVALID, email);
        }
    }

    public String getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
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