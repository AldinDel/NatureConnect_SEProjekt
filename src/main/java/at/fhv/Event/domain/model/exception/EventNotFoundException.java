package at.fhv.Event.domain.model.exception;

public class EventNotFoundException extends RuntimeException {
    private final Long _eventId;

    public EventNotFoundException(Long eventId) {
        super(String.format("Event with id %s not found", eventId));
        _eventId = eventId;
    }

    public Long get_eventId() {
        return _eventId;
    }
}
