package at.fhv.Event.application.feedback;

import at.fhv.Event.domain.model.feedback.FeedbackRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingParticipantEntity;
import at.fhv.Event.infrastructure.persistence.booking.JpaBookingParticipantRepository;
import at.fhv.Event.domain.model.feedback.Feedback;
import at.fhv.Event.domain.model.feedback.FeedbackType;
import org.springframework.stereotype.Service;
import at.fhv.Event.presentation.ui.dto.FeedbackAdminRow;
import at.fhv.Event.presentation.ui.dto.FeedbackStats;
import java.util.List;

import java.time.LocalDateTime;

@Service
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final JpaBookingParticipantRepository bookingParticipantRepository;


    public FeedbackService(FeedbackRepository feedbackRepository, JpaBookingParticipantRepository bookingParticipantRepository) {
        this.feedbackRepository = feedbackRepository;
        this.bookingParticipantRepository = bookingParticipantRepository;
    }

    public void createCustomerFeedback(
            int rating,
            String comment,
            Long eventId,
            Long bookingParticipantId,
            Long createdByUserId
    ) {
        if (feedbackRepository.existsByBookingParticipantId(bookingParticipantId)) {
            throw new IllegalStateException("Feedback already exists for this participant");
        }

        Feedback feedback = new Feedback();
        feedback.setRating(rating);
        feedback.setComment(comment);
        feedback.setEventId(eventId);
        feedback.setBookingParticipantId(bookingParticipantId);
        feedback.setCreatedByUserId(createdByUserId);
        feedback.setType(FeedbackType.CUSTOMER);
        feedback.setCreatedAt(LocalDateTime.now());

        BookingParticipantEntity participant =
                bookingParticipantRepository.findById(bookingParticipantId)
                        .orElseThrow(() -> new IllegalStateException("Participant not found"));

        feedback.setParticipantName(
                participant.getFirstName() + " " + participant.getLastName()
        );


        feedbackRepository.save(feedback);

    }

    public boolean feedbackExists(Long participantId) {
        return feedbackRepository.existsByBookingParticipantId(participantId);
    }

    public FeedbackStats getFeedbackStats(Long eventId) {

        List<Feedback> feedbacks = feedbackRepository.findByEventId(eventId);

        FeedbackStats stats = new FeedbackStats();

        if (feedbacks.isEmpty()) {
            stats.setAverageRating(0);
            stats.setLowRatingCount(0);
            stats.setCommentCount(0);
            return stats;
        }

        double average = feedbacks.stream()
                .mapToInt(Feedback::getRating)
                .average()
                .orElse(0);

        long lowRatings = feedbacks.stream()
                .filter(f -> f.getRating() <= 2)
                .count();

        long commentCount = feedbacks.stream()
                .filter(f -> f.getComment() != null && !f.getComment().isBlank())
                .count();

        stats.setAverageRating(Math.round(average * 10.0) / 10.0);
        stats.setLowRatingCount(lowRatings);
        stats.setCommentCount(commentCount);

        return stats;
    }

    public List<FeedbackAdminRow> getFeedbackForEvent(
            Long eventId,
            String rating,
            String comments,
            String sort
    ) {

        return feedbackRepository.findByEventId(eventId).stream()


                .filter(f -> {
                    if (rating == null || rating.isBlank()) return true;

                    if ("LOW".equals(rating)) {
                        return f.getRating() <= 2;
                    }

                    if (!rating.matches("\\d")) return true; // 🛡️ wichtig

                    return f.getRating() == Integer.parseInt(rating);
                })



                .filter(f -> {
                    if (comments == null || comments.isBlank()) return true;

                    if ("WITH".equals(comments)) {
                        return f.getComment() != null && !f.getComment().isBlank();
                    }

                    if ("WITHOUT".equals(comments)) {
                        return f.getComment() == null || f.getComment().isBlank();
                    }

                    return true;
                })


                .sorted((a, b) -> {
                    if ("oldest".equals(sort))
                        return a.getCreatedAt().compareTo(b.getCreatedAt());
                    if ("rating_desc".equals(sort))
                        return Integer.compare(b.getRating(), a.getRating());
                    if ("rating_asc".equals(sort))
                        return Integer.compare(a.getRating(), b.getRating());

                    return b.getCreatedAt().compareTo(a.getCreatedAt());
                })


                .map(f -> {
                    FeedbackAdminRow row = new FeedbackAdminRow();

                    row.setParticipantName(
                            f.getParticipantName() != null
                                    ? f.getParticipantName()
                                    : "Unknown participant"
                    );


                    row.setRating(f.getRating());
                    row.setComment(f.getComment());
                    row.setCreatedAt(f.getCreatedAt());

                    return row;
                })
                .toList();
    }

}
