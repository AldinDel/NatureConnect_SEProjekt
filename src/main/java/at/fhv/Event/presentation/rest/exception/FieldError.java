package at.fhv.Event.presentation.rest.exception;

public  class FieldError {
    private final String field;
    private final String message;
    private final String type;
    private final Object rejectedValue;

    public FieldError(String field, String message, String type, Object rejectedValue) {
        this.field = field;
        this.message = message;
        this.type = type;
        this.rejectedValue = rejectedValue;
    }

    public String getField() {
        return field;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public Object getRejectedValue() {
        return rejectedValue;
    }
}
