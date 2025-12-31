package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.domain.model.exception.ValidationErrorFactory;
import at.fhv.Event.domain.model.exception.ValidationErrorType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingValidator {
    private static final String NAME_REGEX = "^[A-Za-zÄÖÜäöüß\\- ]+$";
    private static final String EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
    private static final int MAX_NAME_LENGTH = 50;
    private static final int MAX_EMAIL_LENGTH = 100;
    private static final int MAX_VOUCHER_LENGTH = 50;
    private static final int MAX_NOTES_LENGTH = 250;
    private static final int MIN_AGE = 1;
    private static final int MAX_AGE = 120;
    private static final Logger log = LoggerFactory.getLogger(BookingValidator.class);

    public List<ValidationError> validate(CreateBookingRequest request, Event event, Map<Long, Equipment> equipmentMap, int alreadyBookedSeats) {
        List<ValidationError> errors = new ArrayList<>();
        validateBookerName(request, errors);
        validateBookerEmail(request, errors);
        validateSeats(request, event, alreadyBookedSeats, errors);
        validateParticipants(request, errors);
        validateSpecialNotes(request, errors);
        validateVoucherCode(request, errors);
        validateEquipment(request, event, equipmentMap, errors);
        return errors;
    }

    private void validateBookerName(CreateBookingRequest request, List<ValidationError> errors) {
        String firstName = request.getBookerFirstName();
        String lastName = request.getBookerLastName();

        if (isBlank(firstName)) {
            errors.add(ValidationErrorFactory.required("bookerFirstName"));
        } else {
            if (firstName.length() > MAX_NAME_LENGTH) {
                errors.add(ValidationErrorFactory.tooLong("bookerFirstName", firstName, MAX_NAME_LENGTH));
            }
            if (!firstName.matches(NAME_REGEX)) {
                errors.add(ValidationErrorFactory.invalidFormat("bookerFirstName", firstName));
            }
        }

        if (isBlank(lastName)) {
            errors.add(ValidationErrorFactory.required("bookerLastName"));
        } else {
            if (lastName.length() > MAX_NAME_LENGTH) {
                errors.add(ValidationErrorFactory.tooLong("bookerLastName", lastName, MAX_NAME_LENGTH));
            }
            if (!lastName.matches(NAME_REGEX)) {
                errors.add(ValidationErrorFactory.invalidFormat("bookerLastName", lastName));
            }
        }
    }

    private void validateBookerEmail(CreateBookingRequest request, List<ValidationError> errors) {
        String email = request.getBookerEmail();

        if (isBlank(email)) {
            errors.add(ValidationErrorFactory.required("bookerEmail"));
            return;
        }

        if (!email.matches(EMAIL_REGEX)) {
            errors.add(ValidationErrorFactory.invalidFormat("bookerEmail", email));
        }

        if (email.length() > MAX_EMAIL_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("bookerEmail", email, MAX_EMAIL_LENGTH));
        }
    }

    private void validateSeats(CreateBookingRequest request, Event event, int alreadyBookedSeats, List<ValidationError> errors) {
        int requestedSeats = request.getSeats();

        if (requestedSeats < 1) {
            errors.add(ValidationErrorFactory.outOfRange("seats", requestedSeats, 1, Integer.MAX_VALUE));
        }
    }

    private void validateParticipants(CreateBookingRequest request, List<ValidationError> errors) {
        if (request.getParticipants() == null || request.getParticipants().isEmpty()) {
            return;
        }

        for (int i = 0; i < request.getParticipants().size(); i++) {
            var participant = request.getParticipants().get(i);
            String prefix = "participants[" + i + "]";
            int participantNumber = i + 1;

            validateParticipantName(
                    participant.getFirstName(),
                    prefix + ".firstName",
                    participantNumber,
                    "First",
                    errors
            );

            validateParticipantName(
                    participant.getLastName(),
                    prefix + ".lastName",
                    participantNumber,
                    "Last",
                    errors
            );

            validateParticipantAge(
                    participant.getAge(),
                    prefix + ".age",
                    participantNumber,
                    errors
            );
        }
    }

    private void validateParticipantName(String name, String field, int participantNumber, String nameType, List<ValidationError> errors) {
        if (isBlank(name)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    field,
                    name,
                    String.format("Participant %d: %s name is required", participantNumber, nameType)
            ));
        } else {
            if (name.length() > MAX_NAME_LENGTH) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        field,
                        name,
                        String.format("Participant %d: %s name too long (max %d characters)", participantNumber, nameType, MAX_NAME_LENGTH)
                ));
            }
            if (!name.matches(NAME_REGEX)) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        field,
                        name,
                        String.format("Participant %d: %s name can only contain letters, spaces, and hyphens", participantNumber, nameType)
                ));
            }
        }
    }

    private void validateParticipantAge(int age, String field, int participantNumber, List<ValidationError> errors) {
        if (age < MIN_AGE || age > MAX_AGE) {
            errors.add(ValidationErrorFactory.outOfRange("age", age, MIN_AGE, MAX_AGE));
        }
    }

    private void validateSpecialNotes(CreateBookingRequest request, List<ValidationError> errors) {
        String notes = request.getSpecialNotes();

        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("specialNotes", notes, MAX_NOTES_LENGTH));
        }
    }

    private void validateVoucherCode(CreateBookingRequest request, List<ValidationError> errors) {
        String voucherCode = request.getVoucherCode();

        if (voucherCode != null && voucherCode.length() > MAX_VOUCHER_LENGTH) {
            errors.add(ValidationErrorFactory.tooLong("voucherCode", voucherCode, MAX_VOUCHER_LENGTH));
        }
    }

    private void validateEquipment(CreateBookingRequest request, Event event, Map<Long, Equipment> equipmentMap, List<ValidationError> errors) {
        Map<Long, EventEquipment> eventEquipmentMap = createEventEquipmentMap(event);

        for (var entry : request.getEquipment().entrySet()) {
            Long equipmentId = entry.getKey();
            EquipmentSelection selection = entry.getValue();

            if (!selection.isSelected()) {
                continue;
            }

            String prefix = "equipments[" + equipmentId + "]";
            EventEquipment eventEquipment = eventEquipmentMap.get(equipmentId);

            if (eventEquipment == null) {
                errors.add(new ValidationError(
                        ValidationErrorType.EQUIPMENT_ERROR,
                        prefix,
                        String.valueOf(equipmentId),
                        String.format("Equipment %d is not available for this event", equipmentId)
                ));
                continue;
            }

            validateEquipmentQuantity(
                    eventEquipment,
                    selection.getQuantity(),
                    request.getSeats(),
                    prefix,
                    errors
            );
        }
    }

    private void validateEquipmentQuantity(
            EventEquipment eventEquipment,
            int requestedQuantity,
            int participantCount,
            String prefix,
            List<ValidationError> errors) {

        String equipmentName = eventEquipment.getEquipment().getName();
        int availableStock = eventEquipment.getEquipment().getStock();
        boolean isRequired = eventEquipment.isRequired();

        if (requestedQuantity > availableStock) {
            errors.add(new ValidationError(
                    ValidationErrorType.EQUIPMENT_ERROR,
                    prefix + ".quantity",
                    String.valueOf(requestedQuantity),
                    String.format("%s: maximum available is %d", equipmentName, availableStock)
            ));
        }

        if (requestedQuantity <= 0) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    prefix + ".quantity",
                    String.valueOf(requestedQuantity),
                    String.format("%s: quantity must be at least 1", equipmentName)
            ));
            return;
        }

        if (participantCount > 0 && requestedQuantity > participantCount) {
            errors.add(new ValidationError(
                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                    prefix + ".quantity",
                    String.valueOf(requestedQuantity),
                    String.format("%s cannot be booked for more than the number of participants (%d)", equipmentName, participantCount)
            ));
        }

        if (isRequired && participantCount > availableStock) {
            errors.add(new ValidationError(
                    ValidationErrorType.EQUIPMENT_ERROR,
                    prefix,
                    String.valueOf(participantCount),
                    String.format("%s: required for %d participants but only %d available", equipmentName, participantCount, availableStock)
            ));
        }
    }

    private Map<Long, EventEquipment> createEventEquipmentMap(Event event) {
        Map<Long, EventEquipment> map = new HashMap<>();

        for (EventEquipment eventEquipment : event.getEventEquipments()) {
            Long id = eventEquipment.getEquipment().getId();
            map.put(id, eventEquipment);
        }

        return map;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }
}