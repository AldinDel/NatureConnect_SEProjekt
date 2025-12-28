package at.fhv.Event.domain.model.exception;

public class UserNotActiveException extends DomainException {
    private final Long userId;
    public UserNotActiveException(Long userId) {
        super("USER_005");
        this.userId = userId;
    }

    public Long getUserId() {
        return userId;
    }
}
