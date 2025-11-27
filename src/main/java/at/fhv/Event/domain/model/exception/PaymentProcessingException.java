package at.fhv.Event.domain.model.exception;

public class PaymentProcessingException extends RuntimeException {
    private final Long _bookingId;
    private final String _paymentMethod;


    public PaymentProcessingException(Long bookingId, String paymentMethod, String reason) {
        super(String.format("Payment processing failed for booking %d: %s", bookingId, reason));
        _bookingId = bookingId;
        _paymentMethod = paymentMethod;
    }

    public PaymentProcessingException(Long bookingId, String paymentMethod, String reason, Throwable cause) {
        super(String.format("Payment processing failed for booking %d: %s", bookingId, reason), cause);
        _bookingId = bookingId;
        _paymentMethod = paymentMethod;
    }

    public Long getBookingId() {
        return _bookingId;
    }

    public String getPaymentMethod() {
        return _paymentMethod;
    }
}
