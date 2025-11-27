package at.fhv.Event.rest.exception;

public  class FieldError {
    private final String _field;
    private final String _message;
    private final String _type;
    private final Object _rejectedValue;

    public FieldError(String field, String message, String type, Object rejectedValue) {
        _field = field;
        _message = message;
        _type = type;
        _rejectedValue = rejectedValue;
    }

    public String get_field() {
        return _field;
    }

    public String get_message() {
        return _message;
    }

    public String get_type() {
        return _type;
    }

    public Object get_rejectedValue() {
        return _rejectedValue;
    }
}
