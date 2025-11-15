package at.fhv.Event.domain.model.booking;

import java.time.Instant;
import java.time.OffsetDateTime;

public class Booking {

    private Long id;

    // Foreign keys (MVP: just IDs, no object relations)
    private Long eventId;
    private Long customerId;
    private boolean guest;

    // Contact data
    private String firstName;
    private String lastName;
    private String email;

    // Booking details
    private int seats;
    private BookingStatus status;
    private PaymentMethod paymentMethod;
    private String voucherCode;
    private double voucherValue;

    // Pricing
    private double unitPrice;
    private double totalPrice;

    // Timeline
    private OffsetDateTime confirmedAt;
    private OffsetDateTime cancelledAt;

    // Audit
    private Instant createdAt;
    private Instant updatedAt;

    public Booking() {
    }

    public Booking(Long eventId,
                   Long customerId,
                   boolean guest,
                   String firstName,
                   String lastName,
                   String email,
                   int seats,
                   BookingStatus status,
                   PaymentMethod paymentMethod,
                   String voucherCode,
                   double voucherValue,
                   double unitPrice,
                   double totalPrice,
                   OffsetDateTime confirmedAt,
                   OffsetDateTime cancelledAt) {
        this.eventId = eventId;
        this.customerId = customerId;
        this.guest = guest;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.seats = seats;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.voucherCode = voucherCode;
        this.voucherValue = voucherValue;
        this.unitPrice = unitPrice;
        this.totalPrice = totalPrice;
        this.confirmedAt = confirmedAt;
        this.cancelledAt = cancelledAt;
    }

    // getters + setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public boolean isGuest() {
        return guest;
    }

    public void setGuest(boolean guest) {
        this.guest = guest;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getSeats() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats = seats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public void setVoucherCode(String voucherCode) {
        this.voucherCode = voucherCode;
    }

    public double getVoucherValue() {
        return voucherValue;
    }

    public void setVoucherValue(double voucherValue) {
        this.voucherValue = voucherValue;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public void setConfirmedAt(OffsetDateTime confirmedAt) {
        this.confirmedAt = confirmedAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(OffsetDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}
