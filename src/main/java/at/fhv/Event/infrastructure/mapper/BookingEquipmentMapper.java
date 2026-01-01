package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingEquipmentMapper {
    public BookingEquipment toDomain(BookingEquipmentEntity entity) {
        if (entity == null) {
            return null;
        }
        BookingEquipment domain = new BookingEquipment();
        domain.setId(entity.getId());
        domain.setBookingId(entity.getBooking().getId());
        domain.setEquipmentId(entity.getEquipmentId());
        domain.setQuantity(entity.getQuantity());
        domain.setPricePerUnit(entity.getPricePerUnit());
        domain.setInvoiced(entity.isInvoiced());

        return domain;
    }

    public BookingEquipmentEntity toEntity(BookingEquipment domain, BookingEntity bookingEntity) {
        if (domain == null) {
            return null;
        }

        BookingEquipmentEntity entity = new BookingEquipmentEntity();
        entity.setId(domain.getId());
        entity.setBooking(bookingEntity);
        entity.setEquipmentId(domain.getEquipmentId());
        entity.setQuantity(domain.getQuantity());
        entity.setPricePerUnit(domain.getPricePerUnit());
        entity.setTotalPrice(domain.getTotalPrice().doubleValue());
        entity.setInvoiced(domain.isInvoiced());

        return entity;
    }


}
