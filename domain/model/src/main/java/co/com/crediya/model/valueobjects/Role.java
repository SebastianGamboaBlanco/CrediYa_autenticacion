package co.com.crediya.model.valueobjects;

import java.util.Objects;

public class Role {
    private final Long id;
    private final String name;
    private final String description;

    private Role(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public static Role of(Long id, String name, String description) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("Role ID must be a positive number");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be null or empty");
        }
        
        return new Role(id, name.trim().toLowerCase(), description != null ? description.trim() : null);
    }

    public static Role of(Long id, String name) {
        return of(id, name, null);
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public boolean canRegisterUsers() {
        return id == 1L || id == 2L; // Admin (1) y Asesor (2)
    }

    public boolean isAdmin() {
        return id == 1L;
    }

    public boolean isAsesor() {
        return id == 2L;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name);
    }

    @Override
    public String toString() {
        return name;
    }
}