package at.fhv.Event.domain.model.exception;

public class ValidationErrorFactory {
    public static ValidationError required(String field) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                null,
                "This field is required"
        );
    }

    public static ValidationError invalidFormat(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                "Only letters, spaces, and hyphens are allowed"
        );
    }

    public static ValidationError tooShort(String field, Object value, int minLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("Input is too short: Minimum %d characters", minLength)
        );
    }

    public static ValidationError tooLong(String field, Object value, int maxLength) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("Input is too long : Maximum %d characters", maxLength)
        );
    }

    public static ValidationError outOfRange(String field, Object value, int min, int max) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                String.format("Input isn't in range: Must be between %d and %d", min, max)
        );
    }

    public static ValidationError invalidValue(String field, Object value) {
        return new ValidationError(
                ValidationErrorType.INVALID_INPUT,
                field,
                value,
                "Input is invalid"
        );
    }
}