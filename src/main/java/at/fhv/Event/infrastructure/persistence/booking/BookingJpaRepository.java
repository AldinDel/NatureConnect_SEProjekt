package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {
    List<BookingEntity> findByEventId(Long eventId);

    @Query("SELECT SUM(b.seats) FROM BookingEntity b WHERE b.eventId = :eventId AND b.status = 'CONFIRMED'")
    Integer countConfirmedSeatsForEvent(Long eventId);

    List<BookingEntity> findByStatus(BookingStatus status);

    @Query("SELECT b FROM BookingEntity b WHERE b.bookerEmail = :email")
    List<BookingEntity> findByBookerEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BookingEquipmentEntity e WHERE e.booking.id = :bookingId")
    void deleteEquipmentByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT COALESCE(SUM(b.seats), 0) FROM BookingEntity b WHERE b.eventId = :eventId AND b.status = :status")
    int sumSeatsByEventIdAndStatus(@Param("eventId") Long eventId, @Param("status") BookingStatus status);
}
