package at.fhv.Event.presentation.ui.dto;

import java.time.LocalDateTime;

public class FeedbackAdminRow {

    private String participantName;
    private int rating;
    private String comment;
    private LocalDateTime createdAt;

    public String getParticipantName() {
        return participantName;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
