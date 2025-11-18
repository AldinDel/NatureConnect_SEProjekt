package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import at.fhv.Event.domain.model.booking.BookingStatus;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    // All bookings for an event
    List<BookingEntity> findByEventId(Long eventId);

    // All bookings belonging to a customer (non-guest)
    List<BookingEntity> findByCustomerId(Long customerId);

    // Count seats booked for an event (important for capacity handling!)
    @Query("SELECT SUM(b.seats) FROM BookingEntity b WHERE b.eventId = :eventId AND b.status = 'CONFIRMED'")
    Integer countConfirmedSeatsForEvent(Long eventId);

    // Optional: fetch only confirmed bookings
    List<BookingEntity> findByStatus(BookingStatus status);
}
