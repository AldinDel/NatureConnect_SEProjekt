package at.fhv.Event.presentation.rest.exception;

import at.fhv.Event.domain.model.exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(
            BookingValidationException exception,
            WebRequest request) {

        List<FieldError> fieldErrors = new ArrayList<>();

        for (ValidationError error : exception.getErrors()) {
            FieldError fieldError = new FieldError(
                    error.get_field(),
                    error.get_message(),
                    error.get_type().toString(),
                    error.get_rejectedValue()
            );
            fieldErrors.add(fieldError);
        }

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                exception.getMessage(),
                extractPath(request),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EventFullyBookedException.class)
    public ResponseEntity<ErrorResponse> handleEventFullyBooked(
            EventFullyBookedException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.get_eventId());
        details.put("requestedSeats", exception.get_requestedSeats());
        details.put("availableSeats", exception.get_availableSeats());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Event Capacity Exceeded",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("equipmentId", exception.getEquipmentId());
        details.put("equipmentName", exception.getEquipmentName());
        details.put("requestedQuantity", exception.getRequestedQuantity());
        details.put("availableQuantity", exception.getAvailableQuantity());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Insufficient Equipment Stock",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Booking Not Found",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(
            EventNotFoundException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.get_eventId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Event Not Found",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessingError(
            PaymentProcessingException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());
        details.put("paymentMethod", exception.getPaymentMethod());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.PAYMENT_REQUIRED.value(),
                "Payment Processing Failed",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @ExceptionHandler(EventValidationException.class)
    public ResponseEntity<ErrorResponse> handleEventValidationErrors(
            EventValidationException exception,
            WebRequest request) {

        List<FieldError> fieldErrors = new ArrayList<>();

        for (ValidationError error : exception.getErrors()) {
            FieldError fieldError = new FieldError(
                    error.get_field(),
                    error.get_message(),
                    error.get_type().toString(),
                    error.get_rejectedValue()
            );
            fieldErrors.add(fieldError);
        }

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Event Validation Failed",
                exception.getMessage(),
                extractPath(request),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EventAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleEventAlreadyCancelled(
            EventAlreadyCancelledException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Event Already Cancelled",
                exception.getMessage(),
                extractPath(request),
                details
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EventDateInPastException.class)
    public ResponseEntity<ErrorResponse> handleEventDateInPast(
            EventDateInPastException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());
        details.put("eventDate", exception.getEventDate());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Event Date In Past",
                exception.getMessage(),
                extractPath(request),
                details
        );
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidParticipantRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParticipantRange(
            InvalidParticipantRangeException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("minParticipants", exception.getMinParticipants());
        details.put("maxParticipants", exception.getMaxParticipants());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Participant Range",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EquipmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentNotFound(
            EquipmentNotFoundException exception,
            WebRequest request) {

        Map<String, Object> details = new HashMap<>();
        details.put("equipmentId", exception.getEquipmentId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Equipment Not Found",
                exception.getMessage(),
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Invalid Request",
                exception.getMessage(),
                extractPath(request),
                null
        );

        return ResponseEntity.badRequest().body(response);
    }



    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedError(
            Exception exception,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "An unexpected error occurred. Please try again later.",
                extractPath(request),
                null
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}