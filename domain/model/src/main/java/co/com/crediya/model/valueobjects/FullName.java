package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class FullName {
    private final String firstName;
    private final String lastName;

    private FullName(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public static FullName of(String firstName, String lastName) {
        validateFirstName(firstName);
        validateLastName(lastName);
        return new FullName(firstName, lastName);
    }

    private static void validateFirstName(String firstName) {
        if (firstName == null || firstName.isBlank()) {
            throw new BusinessException(ErrorCode.FIRST_NAME_REQUIRED, firstName);
        }
    }

    private static void validateLastName(String lastName) {
        if (lastName == null || lastName.isBlank()) {
            throw new BusinessException(ErrorCode.LAST_NAME_REQUIRED, lastName);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getFullName() {
        return firstName + " " + lastName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FullName that = (FullName) o;
        return Objects.equals(firstName, that.firstName) && Objects.equals(lastName, that.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }

    @Override
    public String toString() {
        return getFullName();
    }
}