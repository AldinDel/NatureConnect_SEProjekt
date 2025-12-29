package at.fhv.Event.domain.model.exception;

public class InvoiceCreationException extends DomainException {
    private final Long bookingId;
    private final String reason;

    public InvoiceCreationException(Long bookingId, String reason) {
        super("INVOICE_001");
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