package co.com.crediya.model.valueobjects;

import java.util.Objects;

public class RoleId {
    private final Long value;

    private RoleId(Long value) {
        this.value = value;
    }

    public static RoleId of(Long roleId) {
        if (roleId == null || roleId <= 0) {
            throw new IllegalArgumentException("Role ID must be a positive number");
        }
        return new RoleId(roleId);
    }

    public Long getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoleId roleId = (RoleId) o;
        return Objects.equals(value, roleId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}