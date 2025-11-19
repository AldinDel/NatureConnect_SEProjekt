package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.infrastructure.persistence.booking.*;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class BookingMapper {
    public BookingEntity toEntity(Booking domain) {
        BookingEntity entity = new BookingEntity();

        entity.setId(domain.getId());
        entity.setEventId(domain.getEventId());
        entity.setBookerFirstName(domain.getBookerFirstName());
        entity.setBookerLastName(domain.getBookerLastName());
        entity.setBookerEmail(domain.getBookerEmail());
        entity.setSeats(domain.getSeats());
        entity.setAudience(domain.getAudience());
        entity.setStatus(domain.getStatus());
        entity.setPaymentStatus(domain.getPaymentStatus());
        entity.setPaymentMethod(domain.getPaymentMethod());
        entity.setVoucherCode(domain.getVoucherCode());
        entity.setDiscountAmount(domain.getDiscountAmount());
        entity.setTotalPrice(domain.getTotalPrice());
        entity.setSpecialNotes(domain.getSpecialNotes());
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : java.time.Instant.now());

        if (domain.getParticipants() != null) {
            var participantEntities = domain.getParticipants().stream()
                    .map(p -> toParticipantEntity(p, entity))
                    .collect(Collectors.toList());

            entity.setParticipants(participantEntities);
        }

        if (domain.getEquipment() != null) {
            var equipmentEntities = domain.getEquipment().stream()
                    .map(eq -> toEquipmentEntity(eq, entity))
                    .collect(Collectors.toList());

            entity.setEquipment(equipmentEntities);
        }
        return entity;
    }

    private BookingParticipantEntity toParticipantEntity(BookingParticipant p, BookingEntity booking) {
        BookingParticipantEntity entity = new BookingParticipantEntity();

        entity.setId(p.getId());
        entity.setFirstName(p.getFirstName());
        entity.setLastName(p.getLastName());
        entity.setAge(p.getAge());
        entity.setBooking(booking);

        return entity;
    }

    private BookingEquipmentEntity toEquipmentEntity(BookingEquipment eq, BookingEntity booking) {
        BookingEquipmentEntity entity = new BookingEquipmentEntity();

        entity.setId(eq.getId());
        entity.setEquipmentId(eq.getEquipmentId());
        entity.setQuantity(eq.getQuantity());
        entity.setUnitPrice(eq.getUnitPrice());
        entity.setTotalPrice(eq.getTotalPrice());
        entity.setBooking(booking);

        return entity;
    }

    public Booking toDomain(BookingEntity entity) {
        Booking domain = new Booking(
                entity.getEventId(),
                entity.getBookerFirstName(),
                entity.getBookerLastName(),
                entity.getBookerEmail(),
                entity.getSeats(),
                entity.getAudience(),
                entity.getStatus(),
                entity.getPaymentStatus(),
                entity.getPaymentMethod(),
                entity.getVoucherCode(),
                entity.getDiscountAmount() == null ? 0.0 : entity.getDiscountAmount(),
                entity.getTotalPrice(),
                entity.getSpecialNotes(),
                null,
                null
        );

        domain.setId(entity.getId());
        domain.setCreatedAt(entity.getCreatedAt());


        if (entity.getParticipants() != null) {
            var domainParticipants = entity.getParticipants().stream()
                    .map(this::toParticipantDomain)
                    .collect(Collectors.toList());

            domain.setParticipants(domainParticipants);
        }

        if (entity.getEquipment() != null) {
            var domainEquipment = entity.getEquipment().stream()
                    .map(this::toEquipmentDomain)
                    .collect(Collectors.toList());

            domain.setEquipment(domainEquipment);
        }

        return domain;
    }

    private BookingParticipant toParticipantDomain(BookingParticipantEntity e) {
        BookingParticipant domain = new BookingParticipant(
                e.getFirstName(),
                e.getLastName(),
                e.getAge()
        );
        domain.setId(e.getId());
        return domain;
    }

    private BookingEquipment toEquipmentDomain(BookingEquipmentEntity e) {
        BookingEquipment domain = new BookingEquipment(
                e.getEquipmentId(),
                e.getQuantity(),
                e.getUnitPrice()
        );
        domain.setId(e.getId());
        domain.setTotalPrice(e.getTotalPrice());
        return domain;
    }
}
