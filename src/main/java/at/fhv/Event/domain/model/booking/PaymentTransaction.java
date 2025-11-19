package at.fhv.Event.domain.model.booking;

import java.time.Instant;
public class PaymentTransaction {

    private Long id;
    private Long bookingId;
    private PaymentMethod method;
    private double amount;
    private TransactionStatus status;
    private String transactionReference;
    private Instant createdAt;

    public PaymentTransaction(Long bookingId, PaymentMethod method, double amount) {
        this.bookingId = bookingId;
        this.method = method;
        this.amount = amount;
        this.status = TransactionStatus.PENDING;
        this.createdAt = Instant.now();
    }

    public void markSuccess() {
        this.status = TransactionStatus.SUCCESS;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public void setMethod(PaymentMethod method) {
        this.method = method;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}