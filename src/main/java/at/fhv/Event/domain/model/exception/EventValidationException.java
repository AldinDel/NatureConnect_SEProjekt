package at.fhv.Event.domain.model.exception;

import java.util.ArrayList;
import java.util.List;

public class EventValidationException extends RuntimeException {
    private final List<ValidationError> _errors;

    public EventValidationException(List<ValidationError> errors) {
        super(constructErrorMessage(errors));
        _errors = new ArrayList<>(errors);
    }

    public List<ValidationError> getErrors() {
        return new ArrayList<>(_errors);
    }

    private static String constructErrorMessage(List<ValidationError> errors) {
        if (errors.isEmpty()) return "Validation failed";
        if (errors.size() == 1) return errors.get(0).get_message();
        return String.format("Validation failed with %d errors", errors.size());
    }
}