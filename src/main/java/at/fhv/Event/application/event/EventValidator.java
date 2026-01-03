package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.domain.model.exception.ValidationErrorFactory;
import at.fhv.Event.domain.model.exception.ValidationErrorType;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class EventValidator {
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_DESCRIPTION_LENGTH = 5000;
    private static final int MAX_LOCATION_LENGTH = 255;

    public List<ValidationError> validate(CreateEventRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        validateTitle(request.getTitle(), errors);
        validateDescription(request.getDescription(), errors);
        validateDate(request.getDate(), errors);
        validateTimeRange(request.getStartTime(), request.getEndTime(), errors);
        validateLocation(request.getLocation(), errors);
        validateParticipantRange(request.getMinParticipants(), request.getMaxParticipants(), errors);
        validatePrice(request.getPrice(), errors);

        return errors;
    }

    public List<ValidationError> validate(UpdateEventRequest request) {
        List<ValidationError> errors = new ArrayList<>();

        validateTitle(request.getTitle(), errors);
        validateDescription(request.getDescription(), errors);
        validateDate(request.getDate(), errors);
        validateTimeRange(request.getStartTime(), request.getEndTime(), errors);
        validateLocation(request.getLocation(), errors);
        validateParticipantRange(request.getMinParticipants(), request.getMaxParticipants(), errors);
        validatePrice(request.getPrice(), errors);

        return errors;
    }

    private void validateTitle(String title, List<ValidationError> errors) {
        if (isBlank(title)) {
            errors.add(ValidationErrorFactory.required("title"));
        } else if (title.length() > MAX_TITLE_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("title", title, MAX_TITLE_LENGTH));
        }
    }

    private void validateDate(LocalDate date, List<ValidationError> errors) {
        if (date == null) {
            errors.add(ValidationErrorFactory.required("date"));
        } else if (date.isBefore(LocalDate.now())) {
            errors.add(new ValidationError(
                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                    "date",
                    date,
                    "Event date cannot be in the past"
            ));
        }
    }

    private void validateTimeRange(LocalTime start, LocalTime end, List<ValidationError> errors) {
        if (start == null || end == null) {
            errors.add(ValidationErrorFactory.required("start, end"));
        } else if (!start.isBefore(end)) {
            errors.add(new ValidationError(
                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                    "time",
                    start + " - " + end,
                    "Start time must be before end time"
            ));
        }
    }

    private void validateParticipantRange(Integer min, Integer max, List<ValidationError> errors) {
        if (min == null || max == null) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "participants",
                    null,
                    "Min and max participants are required"
            ));
            return;
        }

        if (min < 1) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "minParticipants",
                    min,
                    "Minimum participants must be at least 1"
            ));
        }

        if (min > max) {
            errors.add(new ValidationError(
                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                    "participants",
                    "min=" + min + ", max=" + max,
                    "Minimum participants cannot exceed maximum participants"
            ));
        }
    }

    private void validatePrice(BigDecimal price, List<ValidationError> errors) {
        if (price == null) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "price",
                    null,
                    "Price is required"
            ));
        } else if (price.compareTo(BigDecimal.ZERO) < 0) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "price",
                    price,
                    "Price cannot be negative"
            ));
        }
    }

    private void validateLocation(String location, List<ValidationError> errors) {
        if (isBlank(location)) {
            errors.add(ValidationErrorFactory.required("location"));
        } else if (location.length() > MAX_LOCATION_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("location", location, MAX_LOCATION_LENGTH));
        }
    }

    private void validateDescription(String description, List<ValidationError> errors) {
        if (description != null && description.length() > MAX_DESCRIPTION_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("description", description, MAX_DESCRIPTION_LENGTH));
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}