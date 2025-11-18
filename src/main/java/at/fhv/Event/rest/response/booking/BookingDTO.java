package at.fhv.Event.rest.response.booking;

import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.booking.PaymentMethod;

import java.time.OffsetDateTime;

public class BookingDTO {

    private Long id;
    private Long eventId;
    private String firstName;
    private String lastName;
    private String email;
    private int seats;
    private double totalPrice;
    private BookingStatus status;

    public BookingDTO() {
    }
    public BookingDTO(Long id, Long eventId, Long customerId, boolean guest,
                      String firstName, String lastName, String email,
                      int seats, BookingStatus status, PaymentMethod paymentMethod,
                      String voucherCode, double voucherValue, double unitPrice,
                      double totalPrice, OffsetDateTime confirmedAt,
                      OffsetDateTime cancelledAt) {

        this.id = id;
        this.eventId = eventId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = status;
    }


    // getters + setters...

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

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
