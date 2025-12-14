package at.fhv.Event.presentation.rest.response.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;

import java.time.LocalDate;
import java.util.List;

public class BookingWithEventDTO {

    private final Long id;
    private final int seats;
    private final double totalPrice;
    private final double paidAmount;
    private final PaymentStatus paymentStatus;
    private final List<BookingEquipment> equipment;
    private final Booking booking;
    private final EventDetailDTO event;

    public BookingWithEventDTO(Booking booking, EventDetailDTO event) {
        this.booking = booking;
        this.event = event;

        this.id = booking.getId();
        this.seats = booking.getSeats();
        this.totalPrice = booking.getTotalPrice();
        this.paidAmount = booking.getPaidAmount();
        this.paymentStatus = booking.getPaymentStatus();
        this.equipment = booking.getEquipment();
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

    public double getPaidAmount() {
        return paidAmount;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public List<BookingEquipment> getEquipment() {
        return equipment;
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
        LocalDate eventDate = event.date(); // Record getter
        LocalDate today = LocalDate.now();
        return !eventDate.isAfter(today);
    }


    public Boolean getInactive() {
        return getCancelled() || getExpired();
    }
}
