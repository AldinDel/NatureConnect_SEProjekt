package at.fhv.Event.domain.model.feedback;

import java.time.LocalDateTime;

public class Feedback {

    private Long id;
    private int rating;
    private String comment;
    private Long eventId;
    private Long bookingParticipantId;
    private Long createdByUserId;
    private FeedbackType type;
    private LocalDateTime createdAt;


    public Feedback() {}

    public Feedback(
            int rating,
            String comment,
            Long eventId,
            Long bookingParticipantId,
            FeedbackType type,
            LocalDateTime createdAt
    ) {
        this.rating = rating;
        this.comment = comment;
        this.eventId = eventId;
        this.bookingParticipantId = bookingParticipantId;
        this.type = type;
        this.createdAt = createdAt;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getBookingParticipantId() {
        return bookingParticipantId;
    }

    public void setBookingParticipantId(Long bookingParticipantId) {
        this.bookingParticipantId = bookingParticipantId;
    }

    public Long getCreatedByUserId() {
        return createdByUserId;
    }

    public void setCreatedByUserId(Long createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public FeedbackType getType() {
        return type;
    }

    public void setType(FeedbackType type) {
        this.type = type;
    }

}
