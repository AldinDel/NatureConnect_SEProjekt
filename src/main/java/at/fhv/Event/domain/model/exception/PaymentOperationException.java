package at.fhv.Event.domain.model.exception;

public class PaymentOperationException extends DomainException {
    private final Long bookingId;

    public PaymentOperationException(Long bookingId, String errorCode, String message) {
        super("PAYMENT_002");
        this.bookingId = bookingId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getReason() {
        return getMessage();
    }
}
