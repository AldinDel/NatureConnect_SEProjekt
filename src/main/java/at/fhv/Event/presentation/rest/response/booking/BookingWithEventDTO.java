package at.fhv.Event.presentation.rest.response.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;

import java.util.List;
public class BookingWithEventDTO {
    private final Long id;
    private final int seats;
    private final double totalPrice;
    private final double paidAmount;
    private final PaymentStatus paymentStatus;
    private final List<BookingEquipment> equipment;
    private final EventDetailDTO event;

    private final boolean expired;
    private final boolean inactive;
    private final boolean cancelled;

    public BookingWithEventDTO(
            Booking booking,
            EventDetailDTO event,
            boolean expired,
            boolean inactive,
            boolean cancelled
    ) {
        this.id = booking.getId();
        this.seats = booking.getSeats();
        this.totalPrice = booking.getTotalPrice();
        this.paidAmount = booking.getPaidAmount();
        this.paymentStatus = booking.getPaymentStatus();
        this.equipment = booking.getEquipment();
        this.event = event;
        this.expired = expired;
        this.inactive = inactive;
        this.cancelled = cancelled;
    }

    public Boolean getCancelled() {
        return cancelled;
    }


    public boolean isExpired() {
        return expired;
    }

    public boolean isInactive() {
        return inactive;
    }

    public EventDetailDTO getEvent() {
        return event;
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
}
