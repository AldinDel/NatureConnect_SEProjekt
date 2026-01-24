package at.fhv.Event.domain.model.exception;

public class ParticipantNotFoundException extends DomainException {

    private final Long participantId;

    public ParticipantNotFoundException(Long participantId) {
        super("CHECKIN_001");
        this.participantId = participantId;
    }

    public Long getParticipantId() {
        return participantId;
    }
}
