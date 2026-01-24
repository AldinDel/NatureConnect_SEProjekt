package at.fhv.Event.domain.model.exception;

public class ParticipantAlreadyCheckedOutException extends DomainException {

    private final Long participantId;

    public ParticipantAlreadyCheckedOutException(Long participantId) {
        super("CHECKIN_003");
        this.participantId = participantId;
    }

    public Long getParticipantId() {
        return participantId;
    }
}
