package at.fhv.Event.domain.model.exception;

public class UserNotFoundException extends DomainException {
    private final Long userId;
    public UserNotFoundException(Long userId) {
        super("USER_002");
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
