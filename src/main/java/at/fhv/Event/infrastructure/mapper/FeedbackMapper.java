package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.feedback.Feedback;
import at.fhv.Event.infrastructure.persistence.feedback.FeedbackEntity;

public class FeedbackMapper {

    public static FeedbackEntity toEntity(Feedback f) {
        FeedbackEntity e = new FeedbackEntity();
        e.setId(f.getId());
        e.setRating(f.getRating());
        e.setComment(f.getComment());
        e.setEventId(f.getEventId());
        e.setBookingParticipantId(f.getBookingParticipantId());
        e.setCreatedByUserId(f.getCreatedByUserId());
        e.setType(f.getType());
        e.setCreatedAt(f.getCreatedAt());
        return e;
    }

    public static Feedback toDomain(FeedbackEntity e) {
        Feedback f = new Feedback();
        f.setId(e.getId());
        f.setRating(e.getRating());
        f.setComment(e.getComment());
        f.setEventId(e.getEventId());
        f.setBookingParticipantId(e.getBookingParticipantId());
        f.setCreatedByUserId(e.getCreatedByUserId());
        f.setType(e.getType());
        f.setCreatedAt(e.getCreatedAt());
        return f;
    }
}
