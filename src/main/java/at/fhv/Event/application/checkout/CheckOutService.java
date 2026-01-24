package at.fhv.Event.application.checkout;

import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.exception.BookingNotFoundException;
import at.fhv.Event.domain.model.exception.ParticipantAlreadyCheckedOutException;
import at.fhv.Event.domain.model.exception.ParticipantNotCheckedInException;
import at.fhv.Event.domain.model.exception.ParticipantNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .orElseThrow(() -> new ParticipantNotFoundException(participantId));

        if (participant.getCheckInStatus() != ParticipantCheckInStatus.CHECKED_IN) {
            throw new ParticipantNotCheckedInException(participantId);
        }

        if (participant.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT) {
            throw new ParticipantAlreadyCheckedOutException(participantId);
        }

        participant.setCheckOutStatus(ParticipantCheckOutStatus.CHECKED_OUT);
        participant.setCheckOutTime(LocalDateTime.now());
        participantRepo.save(participant);

        Long bookingId = participant.getBookingId();

        boolean allCheckedOut = participantRepo.findByBookingId(bookingId).stream()
                .allMatch(p -> p.getCheckOutStatus() == ParticipantCheckOutStatus.CHECKED_OUT);

        if (allCheckedOut) {
            Booking booking = bookingRepo.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
            booking.setBillingReady(true);
            bookingRepo.save(booking);
        }
    }
}
