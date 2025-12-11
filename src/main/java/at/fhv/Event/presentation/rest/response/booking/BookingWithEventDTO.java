package at.fhv.Event.presentation.rest.response.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;

public class BookingWithEventDTO {

    private final Long id;
    private final int seats;
    private final double totalPrice;
    private final Booking booking;
    private final EventDetailDTO event;

    public BookingWithEventDTO(Booking booking, EventDetailDTO event) {
        this.booking = booking;
        this.event = event;

        this.id = booking.getId();
        this.seats = booking.getSeats();
        this.totalPrice = booking.getTotalPrice();
    }

    public Long getId() {
        return id;
    }

    public int getSeats() {
        return seats;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public EventDetailDTO getEvent() {
        return event;
    }

    public Booking getBooking() {
        return booking;
    }

    public Boolean getCancelled() {
        return booking.getStatus() == BookingStatus.CANCELLED;
    }


    public Boolean getExpired() {
        return booking.getStatus() == BookingStatus.EXPIRED;
    }


    public Boolean getInactive() {
        return getCancelled() || getExpired();
    }
}
