package at.fhv.Event.domain.model.exception;

public class AvatarUploadException extends DomainException {
    private final String reason;

    public AvatarUploadException(String reason) {
        super("AVATAR_UPLOAD_FAILED");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}