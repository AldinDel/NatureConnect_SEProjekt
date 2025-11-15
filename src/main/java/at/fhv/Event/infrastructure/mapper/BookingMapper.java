package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingEntity toEntity(Booking booking) {
        BookingEntity entity = new BookingEntity();

        if (booking.getId() != null) {
            // Only set id if not null (for updates)
            // Hibernate will ignore it on persist if using IDENTITY
        }

        entity.setEventId(booking.getEventId());
        entity.setCustomerId(booking.getCustomerId());
        entity.setGuest(booking.isGuest());
        entity.setFirstName(booking.getFirstName());
        entity.setLastName(booking.getLastName());
        entity.setEmail(booking.getEmail());
        entity.setSeats(booking.getSeats());
        entity.setStatus(booking.getStatus());
        entity.setPaymentMethod(booking.getPaymentMethod());
        entity.setVoucherCode(booking.getVoucherCode());
        entity.setVoucherValue(booking.getVoucherValue());
        entity.setUnitPrice(booking.getUnitPrice());
        entity.setTotalPrice(booking.getTotalPrice());
        entity.setConfirmedAt(booking.getConfirmedAt());
        entity.setCancelledAt(booking.getCancelledAt());

        return entity;
    }

    public Booking toDomain(BookingEntity entity) {
        Booking booking = new Booking();

        booking.setId(entity.getId());
        booking.setEventId(entity.getEventId());
        booking.setCustomerId(entity.getCustomerId());
        booking.setGuest(entity.isGuest());
        booking.setFirstName(entity.getFirstName());
        booking.setLastName(entity.getLastName());
        booking.setEmail(entity.getEmail());
        booking.setSeats(entity.getSeats());
        booking.setStatus(entity.getStatus());
        booking.setPaymentMethod(entity.getPaymentMethod());
        booking.setVoucherCode(entity.getVoucherCode());
        if (entity.getVoucherValue() != null) {
            booking.setVoucherValue(entity.getVoucherValue());
        }
        if (entity.getUnitPrice() != null) {
            booking.setUnitPrice(entity.getUnitPrice());
        }
        if (entity.getTotalPrice() != null) {
            booking.setTotalPrice(entity.getTotalPrice());
        }
        booking.setConfirmedAt(entity.getConfirmedAt());
        booking.setCancelledAt(entity.getCancelledAt());
        booking.setCreatedAt(entity.getCreatedAt());
        booking.setUpdatedAt(entity.getUpdatedAt());

        return booking;
    }
}
