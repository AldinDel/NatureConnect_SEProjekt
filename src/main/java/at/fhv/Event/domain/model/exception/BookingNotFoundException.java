package at.fhv.Event.domain.model.exception;

public class BookingNotFoundException extends RuntimeException {
    private final Long _bookingId;

    public BookingNotFoundException(Long bookingId) {
        super(String.format("Booking with id %s not found", bookingId));
        _bookingId = bookingId;
    }

    public Long getBookingId() {
        return _bookingId;
    }
}