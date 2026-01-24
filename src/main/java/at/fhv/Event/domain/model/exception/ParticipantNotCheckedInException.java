package at.fhv.Event.domain.model.exception;

public class ParticipantNotCheckedInException extends DomainException {

    private final Long participantId;

    public ParticipantNotCheckedInException(Long participantId) {
        super("CHECKIN_002");
        this.participantId = participantId;
    }

    public Long getParticipantId() {
        return participantId;
    }
}

