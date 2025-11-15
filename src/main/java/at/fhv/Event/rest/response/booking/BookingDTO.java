package at.fhv.Event.rest.response.booking;

import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.booking.PaymentMethod;
import java.time.OffsetDateTime;

public class BookingDTO {

    private Long id;
    private Long eventId;
    private Long customerId;
    private boolean guest;

    private String firstName;
    private String lastName;
    private String email;

    private int seats;
    private BookingStatus status;
    private PaymentMethod paymentMethod;

    private String voucherCode;
    private double voucherValue;

    private double unitPrice;
    private double totalPrice;

    private OffsetDateTime confirmedAt;
    private OffsetDateTime cancelledAt;

    public BookingDTO(Long id,
                      Long eventId,
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
        this.id = id;
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

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public boolean isGuest() {
        return guest;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public int getSeats() {
        return seats;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public String getVoucherCode() {
        return voucherCode;
    }

    public double getVoucherValue() {
        return voucherValue;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public OffsetDateTime getConfirmedAt() {
        return confirmedAt;
    }

    public OffsetDateTime getCancelledAt() {
        return cancelledAt;
    }
}
