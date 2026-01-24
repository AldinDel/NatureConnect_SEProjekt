package at.fhv.Event.domain.model.exception;

public class CheckOutOperationException extends DomainException {

    private final Long participantId;
    private final String reason;

    public CheckOutOperationException(Long participantId, String reason) {
        super("CHECKIN_004");
        this.participantId = participantId;
        this.reason = reason;
    }

    public Long getParticipantId() {
        return participantId;
    }

    public String getReason() {
        return reason;
    }
}
