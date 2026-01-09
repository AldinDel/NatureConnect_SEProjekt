package at.fhv.Event.domain.model.exception;

public class DomainException extends RuntimeException {
    private final String errorCode;

    protected DomainException(String errorCode) {
        super();
        this.errorCode = errorCode;
    }

    protected DomainException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}