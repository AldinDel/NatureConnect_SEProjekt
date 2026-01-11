package at.fhv.Event.infrastructure.persistence.feedback;

import at.fhv.Event.domain.model.feedback.Feedback;
import at.fhv.Event.domain.model.feedback.FeedbackRepository;
import at.fhv.Event.infrastructure.mapper.FeedbackMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FeedbackRepositoryImpl implements FeedbackRepository {

    private final JpaFeedbackRepository jpa;

    public FeedbackRepositoryImpl(JpaFeedbackRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public void save(Feedback feedback) {
        jpa.save(FeedbackMapper.toEntity(feedback));
    }

    @Override
    public boolean existsByBookingParticipantId(Long bookingParticipantId) {
        return jpa.existsByBookingParticipantId(bookingParticipantId);
    }

    @Override
    public List<Feedback> findByEventId(Long eventId) {
        return jpa.findByEventId(eventId).stream()
                .map(FeedbackMapper::toDomain)
                .toList();
    }
}
