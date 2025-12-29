package at.fhv.Event.domain.model.exception;

public class PaymentOperationException extends DomainException {
    private final Long bookingId;
    private final String reason;

    public PaymentOperationException(Long bookingId, String reason) {
        super("PAYMENT_002");
        this.bookingId = bookingId;
        this.reason = reason;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getReason() {
        return reason;
    }
}
