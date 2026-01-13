package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingParticipantEntity;
import org.springframework.stereotype.Component;
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
        entity.setPaidAmount(domain.getPaidAmount());
        entity.setSpecialNotes(domain.getSpecialNotes());
        entity.setHikeRouteKey(domain.getHikeRouteKey());
        entity.setCreatedAt(domain.getCreatedAt() != null ? domain.getCreatedAt() : java.time.Instant.now());
        entity.setBillingReady(domain.isBillingReady());

        entity.getParticipants().clear();
        if (domain.getParticipants() != null) {
            for (BookingParticipant p : domain.getParticipants()) {
                BookingParticipantEntity pe = new BookingParticipantEntity();
                pe.setFirstName(p.getFirstName());
                pe.setLastName(p.getLastName());
                pe.setAge(p.getAge());
                pe.setCheckInStatus(p.getCheckInStatus());
                pe.setCheckOutStatus(p.getCheckOutStatus());
                pe.setBooking(entity);
                entity.getParticipants().add(pe);
            }
        }

        entity.getEquipment().clear();
        if (domain.getEquipment() != null) {
            for (BookingEquipment eq : domain.getEquipment()) {
                BookingEquipmentEntity ee = new BookingEquipmentEntity();
                ee.setEquipmentId(eq.getEquipmentId());
                ee.setQuantity(eq.getQuantity());
                ee.setPricePerUnit(eq.getPricePerUnit());
                ee.setTotalPrice(eq.getTotalPrice().doubleValue());
                ee.setInvoiced(eq.isInvoiced());
                ee.setBooking(entity);
                entity.getEquipment().add(ee);
            }
        }
        return entity;
    }

    public Booking toDomain(BookingEntity entity) {
        Booking domain = new Booking();

        domain.setId(entity.getId());
        domain.setEventId(entity.getEventId());
        domain.setBookerFirstName(entity.getBookerFirstName());
        domain.setBookerLastName(entity.getBookerLastName());
        domain.setBookerEmail(entity.getBookerEmail());
        domain.setSeats(entity.getSeats());
        domain.setAudience(entity.getAudience());
        domain.setStatus(entity.getStatus());
        domain.setPaymentStatus(entity.getPaymentStatus());
        domain.setPaymentMethod(entity.getPaymentMethod());
        domain.setVoucherCode(entity.getVoucherCode());
        domain.setDiscountAmount(entity.getDiscountAmount() == null ? 0.0 : entity.getDiscountAmount());
        domain.setTotalPrice(entity.getTotalPrice());
        domain.setPaidAmount(entity.getPaidAmount() == null ? 0.0 : entity.getPaidAmount());
        domain.setSpecialNotes(entity.getSpecialNotes());
        domain.setHikeRouteKey(entity.getHikeRouteKey());
        domain.setCreatedAt(entity.getCreatedAt());
        domain.setBillingReady(entity.isBillingReady());

        if (entity.getParticipants() != null) {
            domain.setParticipants(
                    entity.getParticipants().stream()
                            .map(e -> new BookingParticipant(
                                    e.getId(),
                                    entity.getId(),
                                    e.getFirstName(),
                                    e.getLastName(),
                                    e.getAge(),
                                    e.getCheckInStatus(),
                                    e.getCheckOutStatus(),
                                    e.getCheckOutTime()
                            ))
                            .toList()
            );
        }

        if (entity.getEquipment() != null) {
            domain.setEquipment(
                    entity.getEquipment().stream()
                            .map(e -> new BookingEquipment(
                                    entity.getId(),
                                    e.getEquipmentId(),
                                    e.getQuantity(),
                                    e.getPricePerUnit()
                            ))
                            .toList()
            );
        }

        return domain;
    }

    public void updateEntity(BookingEntity entity, Booking domain) {
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
        entity.setPaidAmount(domain.getPaidAmount());
        entity.setSpecialNotes(domain.getSpecialNotes());
        entity.setHikeRouteKey(domain.getHikeRouteKey());
        entity.setBillingReady(domain.isBillingReady());

        entity.getParticipants().clear();
        if (domain.getParticipants() != null) {
            for (BookingParticipant p : domain.getParticipants()) {
                BookingParticipantEntity pe = new BookingParticipantEntity();
                pe.setFirstName(p.getFirstName());
                pe.setLastName(p.getLastName());
                pe.setAge(p.getAge());
                pe.setCheckInStatus(p.getCheckInStatus());
                pe.setCheckOutStatus(p.getCheckOutStatus());
                pe.setBooking(entity);
                entity.getParticipants().add(pe);
            }
        }

        if (domain.getEquipment() != null) {
            entity.getEquipment().removeIf(existing ->
                    domain.getEquipment().stream()
                            .noneMatch(domainEq -> domainEq.getEquipmentId().equals(existing.getEquipmentId()))
            );

            for (BookingEquipment eq : domain.getEquipment()) {
                BookingEquipmentEntity existing = entity.getEquipment().stream()
                        .filter(e -> e.getEquipmentId().equals(eq.getEquipmentId()))
                        .findFirst()
                        .orElse(null);

                if (existing != null) {
                    existing.setQuantity(eq.getQuantity());
                    existing.setPricePerUnit(eq.getPricePerUnit());
                    existing.setTotalPrice(eq.getTotalPrice().doubleValue());
                    existing.setInvoiced(eq.isInvoiced());
                } else {
                    BookingEquipmentEntity ee = new BookingEquipmentEntity();
                    ee.setEquipmentId(eq.getEquipmentId());
                    ee.setQuantity(eq.getQuantity());
                    ee.setPricePerUnit(eq.getPricePerUnit());
                    ee.setTotalPrice(eq.getTotalPrice().doubleValue());
                    ee.setInvoiced(eq.isInvoiced());
                    ee.setBooking(entity);
                    entity.getEquipment().add(ee);
                }
            }
        } else {
            entity.getEquipment().clear();
        }
    }

}
