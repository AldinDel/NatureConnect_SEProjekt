package at.fhv.Event.domain.model.booking;

import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;

import java.util.List;
import java.util.Optional;

public interface BookingEquipmentRepository {
    List<BookingEquipment> findNotYetInvoicedByBookingId(Long bookingId);
    List<BookingEquipment> findByBookingId(Long bookingId);
    Optional<BookingEquipment> findById(Long id);
    BookingEquipment save(BookingEquipment bookingEquipment, BookingEntity bookingEntity);
    List<BookingEquipment> saveAll(List<BookingEquipment> equipment, BookingEntity bookingEntity);
}
