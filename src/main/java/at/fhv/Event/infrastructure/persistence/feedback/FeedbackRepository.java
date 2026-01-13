package at.fhv.Event.infrastructure.persistence.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository
        extends JpaRepository<FeedbackEntity, Long> {

    boolean existsByBookingParticipantId(Long bookingParticipantId);

    List<FeedbackEntity> findByEventId(Long eventId);
}
