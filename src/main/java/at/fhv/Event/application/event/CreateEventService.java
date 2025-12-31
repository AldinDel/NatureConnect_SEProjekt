package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.CreateEventRequest;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class CreateEventService {
    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventMapperDTO mapper;
    private final EventValidator eventValidator;

    public CreateEventService(EventRepository eventRepository,
                              EquipmentRepository equipmentRepository,
                              EventMapperDTO mapper,
                              EventValidator eventValidator) {
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
        this.eventValidator = eventValidator;
    }

    @Transactional
    public EventDetailDTO createEvent(CreateEventRequest req) {
        List<ValidationError> errors = eventValidator.validate(req);
        if (!errors.isEmpty()) {
            throw new EventValidationException(errors);
        }

        Set<EventEquipment> ees = new HashSet<>();

        if (req.getEquipments() != null && !req.getEquipments().isEmpty()) {
            for (var eqReq : req.getEquipments()) {
                if (eqReq.getName() == null || eqReq.getName().isBlank()) {
                    continue;
                }
                if (eqReq.isRentable()) {
                    if (eqReq.getUnitPrice() == null) {
                        throw new EventValidationException(List.of(
                                new ValidationError(
                                        ValidationErrorType.BUSINESS_RULE_VIOLATION,
                                        "equipment.unitPrice",
                                        eqReq.getName(),
                                        "Unit price is required when equipment is rentable: " + eqReq.getName()
                                )
                        ));
                    }
                    if (eqReq.getStock() == null) {
                        throw new EventValidationException(List.of(
                                new ValidationError(
                                        ValidationErrorType.BUSINESS_RULE_VIOLATION,
                                        "equipment.stock",
                                        eqReq.getName(),
                                        "Stock is required when equipment is rentable: " + eqReq.getName()
                                )
                        ));
                    }
                }
                Equipment equipment;
                if (eqReq.getId() == null) {
                    equipment = new Equipment(
                            null,
                            eqReq.getName(),
                            eqReq.getUnitPrice(),
                            eqReq.isRentable(),
                            eqReq.getStock()
                    );
                    equipment = equipmentRepository.save(equipment);
                } else {
                    equipment = equipmentRepository.findById(eqReq.getId())
                            .orElseThrow(() -> new EquipmentNotFoundException(eqReq.getId()));
                }

                EventEquipment eventEquipment = new EventEquipment(equipment, eqReq.isRequired());
                ees.add(eventEquipment);
            }
        }

        Set<Equipment> required = new HashSet<>();
        if (req.getRequiredEquipmentIds() != null) {
            for (Long id : req.getRequiredEquipmentIds()) {
                Equipment equipment = equipmentRepository.findById(id)
                        .orElseThrow(() -> new EquipmentNotFoundException(id));
                required.add(equipment);
            }
        }

        Set<Equipment> optional = new HashSet<>();
        if (req.getOptionalEquipmentIds() != null) {
            for (Long id : req.getOptionalEquipmentIds()) {
                Equipment equipment = equipmentRepository.findById(id)
                        .orElseThrow(() -> new EquipmentNotFoundException(id));
                optional.add(equipment);
            }
        }

        for (Equipment eq : required) {
            ees.add(new EventEquipment(eq, true));
        }
        for (Equipment eq : optional) {
            ees.add(new EventEquipment(eq, false));
        }

        Difficulty diff = req.getDifficulty() != null
                ? Difficulty.valueOf(req.getDifficulty().toUpperCase())
                : null;

        Event domain = new Event(
                null,
                req.getTitle(),
                req.getDescription(),
                req.getOrganizer(),
                req.getCategory(),
                req.getDate(),
                req.getStartTime(),
                req.getEndTime(),
                req.getLocation(),
                diff,
                req.getMinParticipants(),
                req.getMaxParticipants(),
                req.getPrice(),
                req.getImageUrl(),
                EventAudience.valueOf(req.getAudience()),
                ees,
                req.getHikeRouteKeys()
        );

        Event saved = eventRepository.save(domain);
        return mapper.toDetailDTO(saved);
    }
}