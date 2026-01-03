package at.fhv.Event.domain.model.exception;

public class InvalidPasswordException extends DomainException {
    private final String reason;
    public InvalidPasswordException(String reason) {
        super("USER_003");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
