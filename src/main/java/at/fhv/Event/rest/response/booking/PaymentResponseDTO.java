package at.fhv.Event.rest.response.booking;

import at.fhv.Event.domain.model.booking.PaymentMethod;
import at.fhv.Event.domain.model.booking.TransactionStatus;
public class PaymentResponseDTO {

    private Long transactionId;
    private Long bookingId;
    private PaymentMethod method;
    private double amount;
    private TransactionStatus status;

    public PaymentResponseDTO(Long transactionId, Long bookingId, PaymentMethod method,
                              double amount, TransactionStatus status) {
        this.transactionId = transactionId;
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = status;
    }

    // Getters

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public double getAmount() {
        return amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

}