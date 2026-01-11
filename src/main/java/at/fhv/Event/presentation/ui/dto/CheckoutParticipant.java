package at.fhv.Event.presentation.ui.dto;

public class CheckoutParticipant {

    private Long participantId;
    private Long bookingId;

    private String bookerName;
    private String participantName;
    private Integer participantAge;

    private boolean checkedOut;

    private boolean feedbackExists;


    public Long getParticipantId() {
        return participantId;
    }

    public Long getBookingId() {
        return bookingId;
    }

    public String getBookerName() {
        return bookerName;
    }

    public String getParticipantName() {
        return participantName;
    }

    public Integer getParticipantAge() {
        return participantAge;
    }

    public boolean isCheckedOut() {
        return checkedOut;
    }

    public boolean isFeedbackExists() {
        return feedbackExists;
    }

    // ===== SETTERS =====

    public void setParticipantId(Long participantId) {
        this.participantId = participantId;
    }

    public void setBookingId(Long bookingId) {
        this.bookingId = bookingId;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public void setParticipantName(String participantName) {
        this.participantName = participantName;
    }

    public void setParticipantAge(Integer participantAge) {
        this.participantAge = participantAge;
    }

    public void setCheckedOut(boolean checkedOut) {
        this.checkedOut = checkedOut;
    }

    public void setFeedbackExists(boolean feedbackExists) {
        this.feedbackExists = feedbackExists;
    }
}
