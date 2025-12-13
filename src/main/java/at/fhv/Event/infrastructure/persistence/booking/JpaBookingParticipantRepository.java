package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JpaBookingParticipantRepository
        extends JpaRepository<BookingParticipantEntity, Long> {

    List<BookingParticipantEntity> findByBookingId(Long bookingId);
}
