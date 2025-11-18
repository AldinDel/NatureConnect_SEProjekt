package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingEntity toEntity(Booking booking) {
        BookingEntity entity = new BookingEntity();

        entity.setId(booking.getId());
        entity.setEventId(booking.getEventId());
        entity.setCustomerId(booking.getCustomerId());
        entity.setGuest(booking.isGuest());
        entity.setFirstName(booking.getFirstName());
        entity.setLastName(booking.getLastName());
        entity.setEmail(booking.getEmail());
        entity.setSeats(booking.getSeats());
        entity.setAudience(booking.getAudience());
        entity.setStatus(booking.getStatus());
        entity.setPaymentMethod(booking.getPaymentMethod());
        entity.setVoucherCode(booking.getVoucherCode());
        entity.setVoucherValue(booking.getVoucherValue());
        entity.setUnitPrice(booking.getUnitPrice());
        entity.setTotalPrice(booking.getTotalPrice());
        entity.setConfirmedAt(booking.getConfirmedAt());
        entity.setCancelledAt(booking.getCancelledAt());
        entity.setCreatedAt(booking.getCreatedAt());
        entity.setUpdatedAt(booking.getUpdatedAt());

        return entity;
    }

    public Booking toDomain(BookingEntity entity) {
        Booking booking = new Booking(
                entity.getEventId(),
                entity.getCustomerId(),
                entity.isGuest(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getEmail(),
                entity.getSeats(),
                entity.getAudience(),
                entity.getStatus(),
                entity.getPaymentMethod(),
                entity.getVoucherCode(),
                entity.getVoucherValue() == null ? 0.0 : entity.getVoucherValue(),
                entity.getUnitPrice(),
                entity.getTotalPrice(),
                entity.getConfirmedAt(),
                entity.getCancelledAt()
        );

        booking.setId(entity.getId());
        booking.setCreatedAt(entity.getCreatedAt());
        booking.setUpdatedAt(entity.getUpdatedAt());

        return booking;
    }
}
