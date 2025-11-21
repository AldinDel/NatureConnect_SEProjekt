package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import at.fhv.Event.domain.model.booking.BookingStatus;
import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByEventId(Long eventId);

    @Query("SELECT SUM(b.seats) FROM BookingEntity b WHERE b.eventId = :eventId AND b.status = 'CONFIRMED'")
    Integer countConfirmedSeatsForEvent(Long eventId);

    List<BookingEntity> findByStatus(BookingStatus status);
}
