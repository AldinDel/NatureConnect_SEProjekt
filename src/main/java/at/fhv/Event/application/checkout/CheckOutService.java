package at.fhv.Event.application.checkout;

import at.fhv.Event.domain.model.booking.ParticipantStatus;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingJpaRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingParticipantEntity;
import at.fhv.Event.infrastructure.persistence.booking.JpaBookingParticipantRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new RuntimeException("Participant not found"));


        if (participant.getCheckInStatus() != ParticipantStatus.CHECKED_IN) {
            throw new IllegalStateException("Participant must be checked in first");
        }

        if (participant.getCheckOutStatus() == ParticipantStatus.CHECKED_OUT) {
            throw new IllegalStateException("Participant already checked out");
        }

        participant.setCheckOutStatus(ParticipantStatus.CHECKED_OUT);
        participant.setCheckOutTime(LocalDateTime.now());
        participantRepo.save(participant);

        Long bookingId = participant.getBooking().getId();

        boolean allCheckedOut = participantRepo.findByBookingId(bookingId).stream()
                .allMatch(p -> p.getCheckOutStatus() == ParticipantStatus.CHECKED_OUT);


        if (allCheckedOut) {
            BookingEntity booking = bookingRepo.findById(bookingId)
                    .orElseThrow();

            booking.setBillingReady(true);
            bookingRepo.save(booking);
        }
    }
}
