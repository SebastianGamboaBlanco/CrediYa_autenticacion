package co.com.crediya.model.valueobjects;

import co.com.crediya.model.exceptions.BusinessException;
import co.com.crediya.model.exceptions.ErrorCode;
import java.util.Objects;

public class BaseSalary {
    private static final int MIN_SALARY = 0;
    private static final int MAX_SALARY = 15000000;
    
    private final Integer value;

    private BaseSalary(Integer value) {
        this.value = value;
    }

    public static BaseSalary of(Integer salary) {
        validate(salary);
        return new BaseSalary(salary);
    }

    private static void validate(Integer salary) {
        if (salary == null) {
            throw new BusinessException(ErrorCode.SALARY_REQUIRED, "null");
        }

        if (salary < MIN_SALARY || salary > MAX_SALARY) {
            throw new BusinessException(ErrorCode.SALARY_OUT_OF_RANGE, salary, MIN_SALARY, MAX_SALARY);
        }
    }

    public Integer getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseSalary that = (BaseSalary) o;
        return Objects.equals(value, that.value);
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