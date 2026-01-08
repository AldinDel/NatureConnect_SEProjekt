package at.fhv.Event.domain.model.exception;

public class EventNotFoundException extends DomainException {
    private final Long eventId;

    public EventNotFoundException(Long eventId) {
        super("EVENT_001");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    @Deprecated
    public Long get_eventId() {
        return eventId;
    }
}
