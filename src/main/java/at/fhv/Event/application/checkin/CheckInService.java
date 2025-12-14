package at.fhv.Event.application.checkin;

import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.domain.model.booking.BookingParticipantRepository;
import at.fhv.Event.domain.model.booking.ParticipantStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CheckInService {

    private final BookingParticipantRepository participantRepository;

    public CheckInService(BookingParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public void checkIn(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        p.setCheckInStatus(ParticipantStatus.CHECKED_IN);
        participantRepository.save(p);
    }

    public void markNotArrived(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        p.setCheckInStatus(ParticipantStatus.NOT_ARRIVED);
        participantRepository.save(p);
    }
    public void resetStatus(Long participantId) {
        BookingParticipant p = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        p.setCheckInStatus(ParticipantStatus.REGISTERED);
        participantRepository.save(p);
    }

}
