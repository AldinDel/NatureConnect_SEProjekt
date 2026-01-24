package at.fhv.Event.domain.model.exception;

public class ImageUploadException extends DomainException {
    private final String reason;

    public ImageUploadException(String reason) {
        super("IMAGE_UPLOAD_FAILED");
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}