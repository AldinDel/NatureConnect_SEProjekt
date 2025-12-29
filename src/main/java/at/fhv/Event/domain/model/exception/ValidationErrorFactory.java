package at.fhv.Event.domain.model.exception;

public class ValidationErrorFactory {
    public static ValidationError required(String field) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                null,
                String.format("VALIDATION_REQUIRED: The field \"%s\" is required", field)
        );
    }

    public static ValidationError invalidFormat(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_INVALID_FORMAT: The field \"%s\" has an invalid format", field)
        );
    }

    public static ValidationError tooShort(String field, Object value, int minLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_TOO_SHORT: The field \"%s\" is too short (Minimum: %d characters)", field, minLength)
        );
    }

    public static ValidationError tooLong(String field, Object value, int maxLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_TOO_LONG: The field \"%s\" is too long (Maximum: %d characters)", field, maxLength)
        );
    }

    public static ValidationError outOfRange(String field, Object value, int min, int max) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_OUT_OF_RANGE: The value for \"%s\" is outside the allowed range (%d - %d)", field, min, max)
        );
    }

    public static ValidationError invalidValue(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_INVALID_VALUE: The value \"%s\" is not valid for the field \"%s\"", value, field)
        );
    }
}
