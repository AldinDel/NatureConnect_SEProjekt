package at.fhv.Event.domain.model.exception;

public class InvalidParticipantRangeException extends RuntimeException {
    private final int _minParticipants;
    private final int _maxParticipants;

    public InvalidParticipantRangeException(int min, int max) {
        super(String.format("Invalid participant range: min=%d, max=%d (min must be <= max)", min, max));
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