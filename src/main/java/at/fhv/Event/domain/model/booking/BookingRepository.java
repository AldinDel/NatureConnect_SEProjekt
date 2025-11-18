package at.fhv.Event.domain.model.booking;

import java.util.List;
import java.util.Optional;

public interface BookingRepository {

    Booking save(Booking booking);

    Optional<Booking> findById(Long id);

    List<Booking> findAll();

    List<Booking> findByEventId(Long eventId);

    List<Booking> findByCustomerId(Long customerId);

    int countSeatsForEvent(Long eventId);
}
