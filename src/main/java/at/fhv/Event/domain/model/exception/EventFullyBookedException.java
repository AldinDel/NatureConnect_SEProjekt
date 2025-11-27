package at.fhv.Event.domain.model.exception;

public class EventFullyBookedException extends RuntimeException {
    private final Long _eventId;
    private final int _requestedSeats;
    private final int _availableSeats;

    public EventFullyBookedException(Long eventId, int requestedSeats, int availableSeats) {
        super(buildMessage(requestedSeats, availableSeats));
        _eventId = eventId;
        _requestedSeats = requestedSeats;
        _availableSeats = availableSeats;
    }

    private static String buildMessage(int requestedSeats, int availableSeats) {
        if (availableSeats == 0) {
            return "This event is fully booked. sorry :) ";
        }
        return String.format("Only %d spot(s) remaining for this event, but %d were requested.", availableSeats, requestedSeats);
    }

    public Long get_eventId() {
        return _eventId;
    }

    public int get_requestedSeats() {
        return _requestedSeats;
    }

    public int get_availableSeats() {
        return _availableSeats;
    }
}
