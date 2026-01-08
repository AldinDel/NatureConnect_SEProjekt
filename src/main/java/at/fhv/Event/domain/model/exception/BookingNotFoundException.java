package at.fhv.Event.domain.model.exception;

public class BookingNotFoundException extends DomainException {
    private final Long _bookingId;

    public BookingNotFoundException(Long bookingId) {
        super("BOOKING_001");
        _bookingId = bookingId;
    }

    public Long getBookingId() {
        return _bookingId;
    }
}