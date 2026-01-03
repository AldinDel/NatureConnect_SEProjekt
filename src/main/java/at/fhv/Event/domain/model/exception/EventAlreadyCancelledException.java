package at.fhv.Event.domain.model.exception;

public class EventAlreadyCancelledException extends DomainException {
    private final Long _eventId;

    public EventAlreadyCancelledException(Long eventId) {
        super("EVENT_003");
        _eventId = eventId;
    }

    public Long getEventId() {
        return _eventId;
    }
}