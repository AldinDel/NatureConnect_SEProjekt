package at.fhv.Event.domain.model.exception;

import java.time.LocalDate;

public class EventDateInPastException extends DomainException {
    private final Long _eventId;
    private final LocalDate _eventDate;

    public EventDateInPastException(Long eventId, LocalDate eventDate) {
        super("EVENT_004");
        _eventId = eventId;
        _eventDate = eventDate;
    }

    public Long getEventId() {
        return _eventId;
    }

    public LocalDate getEventDate() {
        return _eventDate;
    }
}
