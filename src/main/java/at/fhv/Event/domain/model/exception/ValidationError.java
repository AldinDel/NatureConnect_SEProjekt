package at.fhv.Event.domain.model.exception;

public class ValidationError {
    private final ValidationErrorType _type;
    private final String _field;
    private final String _message;
    private final Object _rejectedValue;

    public ValidationError(ValidationErrorType type, String field, Object rejectedValue, String message) {
        _type = type;
        _field = field;
        _message = message;
        _rejectedValue = null;
    }

    public ValidationErrorType get_type() {
        return _type;
    }

    public String get_field() {
        return _field;
    }

    public String get_message() {
        return _message;
    }

    public Object get_rejectedValue() {
        return _rejectedValue;
    }
}
