package at.fhv.Event.domain.model.exception;

public class EventFullyBookedException extends DomainException {
    private final Long _eventId;
    private final int _requestedSeats;
    private final int _availableSeats;

    public EventFullyBookedException(Long eventId, int requestedSeats, int availableSeats) {
        super("BOOKING_003");
        _eventId = eventId;
        _requestedSeats = requestedSeats;
        _availableSeats = availableSeats;
    }

    public Long getEventId() {
        return _eventId;
    }

    public int getRequestedSeats() {
        return _requestedSeats;
    }

    public int getAvailableSeats() {
        return _availableSeats;
    }

    @Deprecated
    public Long get_eventId() {
        return _eventId;
    }

    @Deprecated
    public int get_requestedSeats() {
        return _requestedSeats;
    }

    @Deprecated
    public int get_availableSeats() {
        return _availableSeats;
    }
}
