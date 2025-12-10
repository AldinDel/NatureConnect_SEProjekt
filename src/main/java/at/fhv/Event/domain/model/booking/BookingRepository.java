package at.fhv.Event.domain.model.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);
    Optional<Booking> findById(Long id);
    List<Booking> findAll();
    List<Booking> findByEventId(Long eventId);
    List<Booking> findByCustomerEmail(String email);
    int countSeatsForEvent(Long eventId);
    Event loadEventForBooking(Long eventId);
    Map<Long, EquipmentEntity> loadEquipmentMap(CreateBookingRequest request);
    int countOccupiedSeatsForEvent(Long eventId);
}
