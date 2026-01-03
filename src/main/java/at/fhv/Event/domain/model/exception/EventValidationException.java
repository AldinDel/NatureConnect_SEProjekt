package at.fhv.Event.domain.model.exception;

import java.util.ArrayList;
import java.util.List;

public class EventValidationException extends DomainException {
    private final List<ValidationError> _errors;

    public EventValidationException(List<ValidationError> errors) {
        super("EVENT_002");
        _errors = new ArrayList<>(errors);
    }

    public List<ValidationError> getErrors() {
        return new ArrayList<>(_errors);
    }

    public int getErrorCount() {
        return _errors.size();
    }

    public boolean hasSingleError() {
        return _errors.size() == 1;
    }

    public ValidationError getFirstError() {
        return _errors.isEmpty() ? null : _errors.get(0);
    }
}