package at.fhv.Event.domain.model.exception;

public class SessionExpiredException extends DomainException {
    private final String sessionId;

    public SessionExpiredException(String sessionId) {
        super("AUTH_002");
        this.sessionId = sessionId;
    }

    public SessionExpiredException() {
        this("unknown");
    }

    public String getSessionId() {
        return sessionId;
    }
}
