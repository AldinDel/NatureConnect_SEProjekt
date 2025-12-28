package at.fhv.Event.domain.model.exception;

public class UnauthorizedAccessException extends DomainException {
    private final String userId;
    private final String resource;
    private final String action;

    public UnauthorizedAccessException(String userId, String resource, String action) {
        super("AUTH_001");
        this.userId = userId;
        this.resource = resource;
        this.action = action;
    }

    public UnauthorizedAccessException(String userId, String action) {
        this(userId, "unknown", action);
    }

    public String getUserId() {
        return userId;
    }

    public String getResource() {
        return resource;
    }

    public String getAction() {
        return action;
    }
}
