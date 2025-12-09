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
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

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

        List<Equipment> required = new ArrayList<>();
        if (req.getRequiredEquipmentIds() != null) {
            for (Long id : req.getRequiredEquipmentIds()) {
                Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException(id));
                required.add(equipment);
            }
        }

        List<Equipment> optional = new ArrayList<>();
        if (req.getOptionalEquipmentIds() != null) {
            for (Long id : req.getOptionalEquipmentIds()) {
                Equipment equipment = equipmentRepository.findById(id).orElseThrow(() -> new EquipmentNotFoundException(id));
                optional.add(equipment);
            }
        }

        List<EventEquipment> ees = new ArrayList<>();
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
                ees
        );

        eventRepository.save(domain);
        return mapper.toDetailDTO(domain);
    }
}