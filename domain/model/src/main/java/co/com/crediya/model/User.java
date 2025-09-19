package co.com.crediya.model;

import co.com.crediya.model.valueobjects.Email;
import co.com.crediya.model.valueobjects.FullName;
import co.com.crediya.model.valueobjects.BaseSalary;

public class User {
    private final FullName fullName;
    private final Email email;
    private final BaseSalary baseSalary;

    private User(Builder builder) {
        this.fullName = builder.fullName;
        this.email = builder.email;
        this.baseSalary = builder.baseSalary;
    }

    public User(String firstName, String lastName, String email, Integer baseSalary) {
        this.fullName = FullName.of(firstName, lastName);
        this.email = Email.of(email);
        this.baseSalary = BaseSalary.of(baseSalary);
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getFirstName() {
        return fullName.getFirstName();
    }

    public String getLastName() {
        return fullName.getLastName();
    }

    public String getEmail() {
        return email.getValue();
    }

    public Integer getBaseSalary() {
        return baseSalary.getValue();
    }

    public FullName getFullName() {
        return fullName;
    }

    public Email getEmailValue() {
        return email;
    }

    public BaseSalary getSalary() {
        return baseSalary;
    }

    public static class Builder {
        private FullName fullName;
        private Email email;
        private BaseSalary baseSalary;

        private Builder() {}

        public Builder withFullName(String firstName, String lastName) {
            this.fullName = FullName.of(firstName, lastName);
            return this;
        }

        public Builder withFullName(FullName fullName) {
            this.fullName = fullName;
            return this;
        }

        public Builder withEmail(String email) {
            this.email = Email.of(email);
            return this;
        }

        public Builder withEmail(Email email) {
            this.email = email;
            return this;
        }

        public Builder withBaseSalary(Integer salary) {
            this.baseSalary = BaseSalary.of(salary);
            return this;
        }

        public Builder withBaseSalary(BaseSalary baseSalary) {
            this.baseSalary = baseSalary;
            return this;
        }

        public User build() {
            if (fullName == null || email == null || baseSalary == null) {
                throw new IllegalArgumentException("All fields are required to build a User");
            }
            return new User(this);
        }
    }
}
