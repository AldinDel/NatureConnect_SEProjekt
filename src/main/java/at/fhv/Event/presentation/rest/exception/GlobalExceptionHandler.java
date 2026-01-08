package at.fhv.Event.presentation.rest.exception;

import at.fhv.Event.application.exception.ErrorMessageService;
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
    private final ErrorMessageService errorMessageService;

    public GlobalExceptionHandler(ErrorMessageService errorMessageService) {
        this.errorMessageService = errorMessageService;
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookingNotFound(
            BookingNotFoundException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getBookingId()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(BookingValidationException.class)
    public ResponseEntity<ErrorResponse> handleBookingValidation(
            BookingValidationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(exception.getErrorCode());

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
                exception.getErrorCode(),
                message,
                extractPath(request),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EventFullyBookedException.class)
    public ResponseEntity<ErrorResponse> handleEventFullyBooked(
            EventFullyBookedException exception,
            WebRequest request) {

        String messageCode = exception.getAvailableSeats() == 0
                ? "BOOKING_003_FULLY"
                : exception.getErrorCode();

        String message = errorMessageService.getMessage(
                messageCode,
                exception.getAvailableSeats(),
                exception.getRequestedSeats()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());
        details.put("requestedSeats", exception.getRequestedSeats());
        details.put("availableSeats", exception.getAvailableSeats());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEventNotFound(
            EventNotFoundException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEventId()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(EventValidationException.class)
    public ResponseEntity<ErrorResponse> handleEventValidation(
            EventValidationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(exception.getErrorCode());

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
                exception.getErrorCode(),
                message,
                extractPath(request),
                fieldErrors
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EventAlreadyCancelledException.class)
    public ResponseEntity<ErrorResponse> handleEventAlreadyCancelled(
            EventAlreadyCancelledException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(exception.getErrorCode());

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(EventDateInPastException.class)
    public ResponseEntity<ErrorResponse> handleEventDateInPast(
            EventDateInPastException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEventDate()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("eventId", exception.getEventId());
        details.put("eventDate", exception.getEventDate());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(InvalidParticipantRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidParticipantRange(
            InvalidParticipantRangeException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getMinParticipants(),
                exception.getMaxParticipants()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("minParticipants", exception.getMinParticipants());
        details.put("maxParticipants", exception.getMaxParticipants());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EquipmentNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentNotFound(
            EquipmentNotFoundException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEquipmentId()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("equipmentId", exception.getEquipmentId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEquipmentName(),
                exception.getAvailableQuantity(),
                exception.getRequestedQuantity()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("equipmentId", exception.getEquipmentId());
        details.put("equipmentName", exception.getEquipmentName());
        details.put("requestedQuantity", exception.getRequestedQuantity());
        details.put("availableQuantity", exception.getAvailableQuantity());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PaymentProcessingException.class)
    public ResponseEntity<ErrorResponse> handlePaymentProcessing(
            PaymentProcessingException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getBookingId(),
                exception.getPaymentMethod()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());
        details.put("paymentMethod", exception.getPaymentMethod());
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.PAYMENT_REQUIRED.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ErrorResponse> handleUnauthorizedAccess(
            UnauthorizedAccessException exception,
            WebRequest request) {

        String messageCode = "unknown".equals(exception.getResource())
                ? "AUTH_001_SIMPLE"
                : exception.getErrorCode();

        String message = errorMessageService.getMessage(
                messageCode,
                exception.getAction(),
                exception.getResource()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("userId", exception.getUserId());
        details.put("resource", exception.getResource());
        details.put("action", exception.getAction());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(
            DuplicateEmailException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEmail()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("email", exception.getEmail());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getUserId()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("userId", exception.getUserId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ErrorResponse> handleInvalidPassword(
            InvalidPasswordException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getReason()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(RoleNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleRoleNotFound(
            RoleNotFoundException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getRoleCode()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("roleCode", exception.getRoleCode());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(UserNotActiveException.class)
    public ResponseEntity<ErrorResponse> handleUserNotActive(
            UserNotActiveException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(exception.getErrorCode());

        Map<String, Object> details = new HashMap<>();
        details.put("userId", exception.getUserId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.FORBIDDEN.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    @ExceptionHandler(SessionExpiredException.class)
    public ResponseEntity<ErrorResponse> handleSessionExpired(
            SessionExpiredException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(exception.getErrorCode());

        Map<String, Object> details = new HashMap<>();
        details.put("sessionId", exception.getSessionId());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BookingOperationException.class)
    public ResponseEntity<ErrorResponse> handleBookingOperation(
            BookingOperationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getOperation(),
                exception.getReason()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());
        details.put("operation", exception.getOperation());
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(PaymentOperationException.class)
    public ResponseEntity<ErrorResponse> handlePaymentOperation(
            PaymentOperationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getBookingId(),
                exception.getReason()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.PAYMENT_REQUIRED.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.status(HttpStatus.PAYMENT_REQUIRED).body(response);
    }

    @ExceptionHandler(InvoiceCreationException.class)
    public ResponseEntity<ErrorResponse> handleInvoiceCreation(
            InvoiceCreationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getBookingId(),
                exception.getReason()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("bookingId", exception.getBookingId());
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(EquipmentCreationException.class)
    public ResponseEntity<ErrorResponse> handleEquipmentCreation(
            EquipmentCreationException exception,
            WebRequest request) {

        String message = errorMessageService.getMessage(
                exception.getErrorCode(),
                exception.getEquipmentName(),
                exception.getReason()
        );

        Map<String, Object> details = new HashMap<>();
        details.put("equipmentName", exception.getEquipmentName());
        details.put("reason", exception.getReason());

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                exception.getErrorCode(),
                message,
                extractPath(request),
                details
        );

        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(
            IllegalArgumentException exception,
            WebRequest request) {

        ErrorResponse response = new ErrorResponse(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "INVALID_REQUEST",
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
                "INTERNAL_ERROR",
                "There was an unexpected error processing your request. Please try again.",
                extractPath(request),
                null
        );
        exception.printStackTrace();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    private String extractPath(WebRequest request) {
        return request.getDescription(false).replace("uri=", "");
    }
}