package at.fhv.Event.presentation.rest.exception;

import java.time.LocalDateTime;

public class ErrorResponse {
    private final LocalDateTime _timestamp;
    private final int _status;
    private final String _error;
    private final String _message;
    private final String _path;
    private final Object _details;

    public ErrorResponse(LocalDateTime timestamp, int status, String error, String message, String path, Object details) {
        _timestamp = timestamp;
        _status = status;
        _error = error;
        _message = message;
        _path = path;
        _details = details;
    }

    public LocalDateTime get_timestamp() {
        return _timestamp;
    }

    public int get_status() {
        return _status;
    }

    public String get_error() {
        return _error;
    }

    public String get_message() {
        return _message;
    }

    public String get_path() {
        return _path;
    }

    public Object get_details() {
        return _details;
    }
}
