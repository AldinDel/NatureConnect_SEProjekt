package at.fhv.Event.domain.model.exception;

public class BookingOperationException extends DomainException {
    private final Long bookingId;
    private final String operation;
    private final String reason;

    public BookingOperationException(Long bookingId, String operation, String reason) {
        super("BOOKING_004");
        this.bookingId = bookingId;
        this.operation = operation;
        this.reason = reason;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getOperation() {
        return operation;
    }

    public String getReason() {
        return reason;
    }
}
