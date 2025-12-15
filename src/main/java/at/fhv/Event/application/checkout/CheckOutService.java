package at.fhv.Event.application.checkout;

import at.fhv.Event.domain.model.booking.ParticipantCheckInStatus;
import at.fhv.Event.domain.model.booking.ParticipantCheckOutStatus;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingJpaRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingParticipantEntity;
import at.fhv.Event.infrastructure.persistence.booking.JpaBookingParticipantRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Transactional
public class CheckOutService {

    private final JpaBookingParticipantRepository participantRepo;
    private final BookingJpaRepository bookingRepo;

    public CheckOutService(
            JpaBookingParticipantRepository participantRepo,
            BookingJpaRepository bookingRepo
    ) {
        this.participantRepo = participantRepo;
        this.bookingRepo = bookingRepo;
    }

    public void checkOut(Long participantId) {

        BookingParticipantEntity participant = participantRepo.findById(participantId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        if (participant.getCheckInStatus() != ParticipantCheckInStatus.CHECKED_IN) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        if (participant.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Participant already checked out");
        }

        participant.setCheckOutStatus(ParticipantCheckOutStatus.CHECKED_OUT);
        participant.setCheckOutTime(LocalDateTime.now());
        participantRepo.save(participant);

        Long bookingId = participant.getBooking().getId();

        boolean allCheckedOut = participantRepo.findByBookingId(bookingId).stream()
                .allMatch(p -> p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT);

        if (allCheckedOut) {
            BookingEntity booking = bookingRepo.findById(bookingId).orElseThrow();
            booking.setBillingReady(true);
            bookingRepo.save(booking);
        }
    }
}
