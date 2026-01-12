package at.fhv.Event.application.event;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventAudience;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EquipmentNotFoundException;
import at.fhv.Event.domain.model.exception.EventValidationException;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.domain.model.exception.ValidationErrorType;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreateEventService {

    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventMapperDTO mapper;
    private final EventValidator eventValidator;
    private final AuditLogService auditLogService;

    public CreateEventService(
            EventRepository eventRepository,
            EquipmentRepository equipmentRepository,
            EventMapperDTO mapper,
            EventValidator eventValidator,
            AuditLogService auditLogService
    ) {
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
        this.eventValidator = eventValidator;
        this.auditLogService = auditLogService;
    }

    @CacheEvict(value = {"events", "eventBatch"}, allEntries = true)
    @Transactional
    public EventDetailDTO createEvent(CreateEventRequest req) {

        List<ValidationError> errors = eventValidator.validate(req);
        if (!errors.isEmpty()) {
            throw new EventValidationException(errors);
        }

        Set<EventEquipment> eventEquipments = new HashSet<>();

        if (req.getEquipments() != null) {
            for (var eqReq : req.getEquipments()) {

                if (eqReq.getName() == null || eqReq.getName().isBlank()) {
                    continue;
                }

                if (eqReq.isRentable() &&
                        (eqReq.getUnitPrice() == null || eqReq.getStock() == null)) {

                    throw new EventValidationException(List.of(
                            new ValidationError(
                                    ValidationErrorType.BUSINESS_RULE_VIOLATION,
                                    "equipment",
                                    eqReq.getName(),
                                    "Rentable equipment requires price and stock"
                            )
                    ));
                }

                Equipment equipment;
                if (eqReq.getId() == null) {
                    equipment = equipmentRepository.save(
                            new Equipment(
                                    null,
                                    eqReq.getName(),
                                    eqReq.getUnitPrice(),
                                    eqReq.isRentable(),
                                    eqReq.getStock()
                            )
                    );
                } else {
                    equipment = equipmentRepository.findById(eqReq.getId())
                            .orElseThrow(() -> new EquipmentNotFoundException(eqReq.getId()));
                }

                eventEquipments.add(
                        new EventEquipment(equipment, eqReq.isRequired())
                );
            }
        }

        Difficulty difficulty = req.getDifficulty() != null
                ? Difficulty.valueOf(req.getDifficulty().toUpperCase())
                : null;

        EventAudience audience = req.getAudience() != null
                ? EventAudience.valueOf(req.getAudience())
                : null;

        LocalDate date = req.isRecurring()
                ? req.getRecurrenceStart()
                : req.getDate();

        Event event = new Event(
                null,
                req.getTitle(),
                req.getDescription(),
                req.getOrganizer(),
                req.getCategory(),
                date,
                req.getStartTime(),
                req.getEndTime(),
                req.getLocation(),
                difficulty,
                req.getMinParticipants(),
                req.getMaxParticipants(),
                req.getPrice(),
                req.getImageUrl(),
                audience,
                eventEquipments,
                req.getHikeRouteKeys()
        );

        if (req.isRecurring()) {
            event.setEndDate(null);
        } else {
            event.setEndDate(req.getEndDate() != null ? req.getEndDate() : req.getDate());
        }

        event.setRecurring(req.isRecurring());
        event.setRecurrenceStart(req.isRecurring() ? req.getRecurrenceStart() : null);
        event.setRecurrenceEnd(req.isRecurring() ? req.getRecurrenceEnd() : null);
        event.setRecurrenceDays(req.isRecurring() ? req.getRecurrenceDays() : null);

        Event saved = eventRepository.save(event);

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            auditLogService.log(
                    auth.getName(),
                    ActionType.CREATE,
                    "Created event: " + saved.getTitle(),
                    "Event",
                    saved.getId(),
                    "Date: " + saved.getDate() + ", Location: " + saved.getLocation()
            );
        }

        return mapper.toDetailDTO(saved);
    }
}
