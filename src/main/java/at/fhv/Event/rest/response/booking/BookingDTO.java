package at.fhv.Event.rest.response.booking;

import at.fhv.Event.domain.model.booking.BookingStatus;

import java.time.Instant;

public class BookingDTO {

    private Long id;
    private Long eventId;

    private String bookerFirstName;
    private String bookerLastName;
    private String bookerEmail;

    private int seats;
    private double totalPrice;
    private BookingStatus status;
    private Instant createdAt;
    private boolean editable;


    public BookingDTO() {
    }

    public BookingDTO(Long id, Long eventId,
                      String bookerFirstName, String bookerLastName, String bookerEmail,
                      int seats, double totalPrice, BookingStatus status, Instant createdAt) {

        this.id = id;
        this.eventId = eventId;
        this.bookerFirstName = bookerFirstName;
        this.bookerLastName = bookerLastName;
        this.bookerEmail = bookerEmail;
        this.seats = seats;
        this.totalPrice = totalPrice;
        this.status = status;
        this.createdAt = createdAt;
    }

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

    public String getBookerFirstName() {
        return bookerFirstName;
    }

    public void setBookerFirstName(String bookerFirstName) {
        this.bookerFirstName = bookerFirstName;
    }

    public String getBookerLastName() {
        return bookerLastName;
    }

    public void setBookerLastName(String bookerLastName) {
        this.bookerLastName = bookerLastName;
    }

    public String getBookerEmail() {
        return bookerEmail;
    }

    public void setBookerEmail(String bookerEmail) {
        this.bookerEmail = bookerEmail;
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

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

}
