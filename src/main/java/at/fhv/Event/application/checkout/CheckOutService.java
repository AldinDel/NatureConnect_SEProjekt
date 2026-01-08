package at.fhv.Event.application.checkout;

import at.fhv.Event.domain.model.booking.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Service
@Transactional
public class CheckOutService {

    private final BookingParticipantRepository participantRepo;
    private final BookingRepository bookingRepo;

    public CheckOutService(BookingParticipantRepository participantRepo, BookingRepository bookingRepo) {
        this.participantRepo = participantRepo;
        this.bookingRepo = bookingRepo;
    }

    public void checkOut(Long participantId) {

        BookingParticipant participant = participantRepo.findById(participantId)
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

        Long bookingId = participant.getBookingId();

        boolean allCheckedOut = participantRepo.findByBookingId(bookingId).stream()
                .allMatch(p -> p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT);

        if (allCheckedOut) {
            Booking booking = bookingRepo.findById(bookingId).orElseThrow();
            booking.setBillingReady(true);
            bookingRepo.save(booking);
        }
    }
}
