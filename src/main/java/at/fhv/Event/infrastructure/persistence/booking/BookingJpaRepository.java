package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingJpaRepository extends JpaRepository<BookingEntity, Long> {

    List<BookingEntity> findByEventId(Long eventId);

    @Query("""
        SELECT COALESCE(SUM(b.seats), 0)
        FROM BookingEntity b
        WHERE b.eventId = :eventId
          AND b.status IN ('CONFIRMED', 'PAID')
    """)
    int countOccupiedSeatsForEvent(@Param("eventId") Long eventId);

    List<BookingEntity> findByStatus(BookingStatus status);

    @Query("SELECT DISTINCT b FROM BookingEntity b LEFT JOIN FETCH b.participants WHERE b.bookerEmail = :email")
    List<BookingEntity> findByBookerEmail(@Param("email") String email);

    @Modifying(clearAutomatically = true)
    @Query("DELETE FROM BookingEquipmentEntity e WHERE e.booking.id = :bookingId")
    void deleteEquipmentByBookingId(@Param("bookingId") Long bookingId);

    @Modifying
    @Query("UPDATE BookingEntity b SET b.status = :status WHERE b.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") BookingStatus status);

    @Modifying
    @Query("""
        UPDATE BookingEntity b 
        SET b.status = 'EXPIRED'
        WHERE b.eventId = :eventId
          AND b.status NOT IN ('CANCELLED', 'EXPIRED')
    """)
    void markExpiredForEvent(@Param("eventId") Long eventId);
}
