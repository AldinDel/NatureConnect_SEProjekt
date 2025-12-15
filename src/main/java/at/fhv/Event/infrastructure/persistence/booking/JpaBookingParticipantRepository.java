package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

import at.fhv.Event.domain.model.booking.ParticipantStatus;

public interface JpaBookingParticipantRepository
        extends JpaRepository<BookingParticipantEntity, Long> {

    List<BookingParticipantEntity> findByBookingId(Long bookingId);

    boolean existsByBooking_IdAndCheckOutStatus(
            Long bookingId,
            ParticipantStatus checkOutStatus
    );
}
