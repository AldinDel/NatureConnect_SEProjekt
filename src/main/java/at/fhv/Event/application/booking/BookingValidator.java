package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.exception.ValidationError;
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
        validateBookerEmail(request,errors);
        validateSeats(request, event, alreadyBookedSeats, errors);
        validateParticipants(request, errors);
        validateSpecialNotes(request, errors);
        validateVoucherCode(request, errors);
        validateEquipment(request, event, equipmentMap, errors);
        return errors;
    }

    private void validateBookerName(CreateBookingRequest request, List<ValidationError> errors) {
        String _firstName = request.getBookerFirstName();
        String _lastName = request.getBookerLastName();
        if (isBlank(_firstName)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "bookerFirstName",
                    "First name is required.",
                    _firstName
            ));
        } else {
            if (_firstName.length() > MAX_NAME_LENGTH) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        "bookerFirstName",
                        "first name can't exceed 50 characters.",
                        _firstName
                ));
            }
            if (!_firstName.matches(NAME_REGEX)) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        "bookerFirstName",
                        "First name can only contain letters, spaces, and dash.",
                        _firstName
                ));
            }
        }
        if (isBlank(_lastName)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "bookerLastName",
                    "Last name is required.",
                    _lastName
            ));
        } else {
            if (_lastName.length() > MAX_NAME_LENGTH) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        "bookerLastName",
                        "Last name can't exceed 50 characters.",
                        _lastName
                ));
            }
            if (!_lastName.matches(NAME_REGEX)) {
                errors.add(new ValidationError(
                        ValidationErrorType.INVALID_INPUT,
                        "bookerLastName",
                        "Last name can only contain letters, spaces, and dash",
                        _lastName
                ));
            }
        }
    }
    private void validateBookerEmail(CreateBookingRequest request, List<ValidationError> errors) {
        String _email = request.getBookerEmail();
        if (isBlank(_email)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "bookerEmail",
                    "Email is required.",
                    _email
            ));
            return;
        }
        if (!_email.matches(EMAIL_REGEX)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "bookerEmail",
                    "Email format is invalid",
                    _email
            ));
        }
        if (_email.length() > MAX_EMAIL_LENGTH) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "bookerEmail",
                    "Email can't exceed 100 characters",
                    _email
            ));
        }
    }

    private void validateSeats(CreateBookingRequest request, Event event, int alreadyBookedSeats, List<ValidationError> errors) {
        int requestedSeats = request.getSeats();
        if (requestedSeats < 1) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "seats",
                    "At least 1 participant required",
                    String.valueOf(requestedSeats)
            ));
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
            validateParticipantName(participant.getFirstName(), prefix + ".firstName",
                    participantNumber, "First", errors);
            validateParticipantName(participant.getLastName(), prefix + ".lastName",
                    participantNumber, "Last", errors);
            validateParticipantAge(participant.getAge(), prefix + ".age",
                    participantNumber, errors);
        }
    }

    private void validateParticipantName(String name, String field, int participantNumber, String nameType, List<ValidationError> errors) {
        if (isBlank(name)) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    field,
                    String.format("Participant %d: %s name is required", participantNumber, nameType),
                    name
            ));
        } else if (name.length() > MAX_NAME_LENGTH) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    field,
                    String.format("Participant %d: %s name too long", participantNumber, nameType),
                    name
            ));
        }
    }

    private void validateParticipantAge(int age, String field, int participantNumber, List<ValidationError> errors) {
        if (age < MIN_AGE || age > MAX_AGE) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    field,
                    String.format("Participant %d: Age must be between 1 and 120", participantNumber),
                    String.valueOf(age)
            ));
        }
    }
    private void validateSpecialNotes(CreateBookingRequest request, List<ValidationError> errors) {
        String notes = request.getSpecialNotes();
        if (notes != null && notes.length() > MAX_NOTES_LENGTH) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "specialNotes",
                    "Special notes can't exceed 250 characters",
                    notes
            ));
        }
    }
    private void validateVoucherCode(CreateBookingRequest request, List<ValidationError> errors) {
        String voucherCode = request.getVoucherCode();
        if (voucherCode != null && voucherCode.length() > MAX_VOUCHER_LENGTH ) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    "voucherCode",
                    "Voucher code can't exceed 50 characters",
                    voucherCode
            ));
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
                        String.format("Equipment %d is not available for this event", equipmentId),
                        String.valueOf(equipmentId)
                )); continue;
            }
            validateEquipmentQuantity(eventEquipment, selection.getQuantity(), request.getSeats(), prefix, errors);
        }
    }
    private void validateEquipmentQuantity(EventEquipment eventEquipment,
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
                    String.valueOf(requestedQuantity), // <-- NEU: Dies ist jetzt rejectedValue (3. Argument)
                    String.format("%s: maximum available is %d", equipmentName, availableStock) // <-- NEU: Dies ist jetzt message (4. Argument)
            ));
        }

        if (requestedQuantity <= 0) {
            errors.add(new ValidationError(
                    ValidationErrorType.INVALID_INPUT,
                    prefix + ".quantity",
                    String.valueOf(requestedQuantity), // <-- NEU: rejectedValue
                    String.format("%s: quantity must be at least 1", equipmentName) // <-- NEU: message
            ));
            return;
        }

        if (participantCount > 0 && requestedQuantity > participantCount) {
            errors.add(new ValidationError(
                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                    prefix + ".quantity",
                    String.format("%s cannot be booked for more than the number of participants (%d)", equipmentName, participantCount),
                    String.valueOf(requestedQuantity)
            ));
        }

        if (isRequired && participantCount > availableStock) {
            errors.add(new ValidationError(
                    ValidationErrorType.EQUIPMENT_ERROR,
                    prefix,
                    String.format("%s: required for %d participants but only %d available", equipmentName, participantCount, availableStock),
                    String.valueOf(participantCount)
            ));
        }
    }

    private Map<Long, EventEquipment> createEventEquipmentMap(Event event) {
        Map<Long, EventEquipment> map = new HashMap<>();
        for (EventEquipment eventEquipment : event.getEventEquipments()) {
            Long id = eventEquipment.getEquipment().getId();
            map.put(id,eventEquipment);
        }
        return map;
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

}
