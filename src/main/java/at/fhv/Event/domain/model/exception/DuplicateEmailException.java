package at.fhv.Event.domain.model.exception;

public class DuplicateEmailException extends DomainException {
    private final String email;
    public DuplicateEmailException(String email) {
        super("USER_001");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
