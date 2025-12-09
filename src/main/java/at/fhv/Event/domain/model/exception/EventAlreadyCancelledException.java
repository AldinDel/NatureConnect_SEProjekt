package at.fhv.Event.domain.model.exception;

public class EventAlreadyCancelledException extends RuntimeException {
    private final Long _eventId;

    public EventAlreadyCancelledException(Long eventId) {
        super(String.format("Event with id %d is already cancelled", eventId));
        _eventId = eventId;
    }

    public Long getEventId() {
        return _eventId;
    }
}