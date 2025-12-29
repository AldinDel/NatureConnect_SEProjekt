package at.fhv.Event.domain.model.exception;

public class ValidationErrorFactory {
    public static ValidationError required(String field) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                null,
                "VALIDATION_REQUIRED"
        );
    }

    public static ValidationError invalidFormat(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                "VALIDATION_INVALID_FORMAT"
        );
    }

    public static ValidationError tooShort(String field, Object value, int minLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_TOO_SHORT: Minimum %d characters", minLength)
        );
    }

    public static ValidationError tooLong(String field, Object value, int maxLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_TOO_LONG: Maximum %d characters", maxLength)
        );
    }

    public static ValidationError outOfRange(String field, Object value, int min, int max) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("VALIDATION_OUT_OF_RANGE: Must be between %d and %d", min, max)
        );
    }

    public static ValidationError invalidValue(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                "VALIDATION_INVALID_VALUE"
        );
    }
}