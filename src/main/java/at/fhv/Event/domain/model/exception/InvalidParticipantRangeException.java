package at.fhv.Event.domain.model.exception;

public class InvalidParticipantRangeException extends DomainException {
    private final int _minParticipants;
    private final int _maxParticipants;

    public InvalidParticipantRangeException(int min, int max) {
        super("EVENT_005");
        _minParticipants = min;
        _maxParticipants = max;
    }

    public int getMinParticipants() {
        return _minParticipants;
    }

    public int getMaxParticipants() {
        return _maxParticipants;
    }
}