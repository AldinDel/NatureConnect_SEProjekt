package at.fhv.Event.presentation.rest.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final LocalDateTime timestamp;
    private final int status;
    private final String error;
    private final String message;
    private final String path;
    private final Object details;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, Object details) {
        this.timestamp = timestamp;
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public Object getDetails() {
        return details;
    }
}
