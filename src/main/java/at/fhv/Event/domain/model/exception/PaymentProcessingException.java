package at.fhv.Event.domain.model.exception;

public class PaymentProcessingException extends DomainException {
    private final Long _bookingId;
    private final String _paymentMethod;
    private final String _reason;


    public PaymentProcessingException(Long bookingId, String paymentMethod, String reason) {
        super("PAYMENT_001", reason);
        _bookingId = bookingId;
        _paymentMethod = paymentMethod;
        _reason = reason;
    }

    public PaymentProcessingException(Long bookingId, String paymentMethod) {
        this(bookingId, paymentMethod, "Payment processing failed");
    }

    public Long getBookingId() {
        return _bookingId;
    }

    public String getPaymentMethod() {
        return _paymentMethod;
    }

    public String getReason() {
        return _reason;
    }
}