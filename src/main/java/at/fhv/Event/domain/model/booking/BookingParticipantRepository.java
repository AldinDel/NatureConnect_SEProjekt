package at.fhv.Event.domain.model.booking;

import java.util.List;
import java.util.Optional;

public interface BookingParticipantRepository {

    Optional<BookingParticipant> findById(Long id);

    List<BookingParticipant> findByBookingId(Long bookingId);

    BookingParticipant save(BookingParticipant participant);
}
