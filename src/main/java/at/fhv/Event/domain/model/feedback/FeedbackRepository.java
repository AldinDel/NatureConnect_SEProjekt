package at.fhv.Event.domain.model.feedback;

import java.util.List;

public interface FeedbackRepository {

    void save(Feedback feedback);

    boolean existsByBookingParticipantId(Long bookingParticipantId);

    List<Feedback> findByEventId(Long eventId);
}
