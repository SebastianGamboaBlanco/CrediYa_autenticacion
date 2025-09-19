package co.com.crediya.model.exceptions;

import reactor.util.function.Tuple3;
import java.util.List;

public class MultipleValidationException extends RuntimeException {
    private final List<Tuple3<String, String, String>> errors;

    public MultipleValidationException(List<Tuple3<String, String, String>> errors) {
        super("Found " + errors.size() + " errors of validate");
        this.errors = errors;
    }

    public List<Tuple3<String, String, String>> getErrors() {
        return errors;
    }
}