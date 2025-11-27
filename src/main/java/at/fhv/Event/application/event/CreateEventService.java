package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CreateEventService {

    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventMapperDTO mapper;

    public CreateEventService(EventRepository eventRepository,
                              EquipmentRepository equipmentRepository,
                              EventMapperDTO mapper) {
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EventDetailDTO createEvent(CreateEventRequest req) {
        List<Equipment> required = req.getRequiredEquipmentIds() == null ? List.of()
                : req.getRequiredEquipmentIds().stream()
                .map(id -> equipmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Equipment not found: " + id)))
                .toList();

        List<Equipment> optional = req.getOptionalEquipmentIds() == null ? List.of()
                : req.getOptionalEquipmentIds().stream()
                .map(id -> equipmentRepository.findById(id)
                        .orElseThrow(() -> new RuntimeException("Equipment not found: " + id)))
                .toList();

        List<EventEquipment> ees = required.stream().map(eq -> new EventEquipment(eq, true)).collect(Collectors.toList());
        ees.addAll(optional.stream().map(eq -> new EventEquipment(eq, false)).toList());

        Difficulty diff = req.getDifficulty() != null ? Difficulty.valueOf(req.getDifficulty().toUpperCase()) : null;

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
                req.getAudience(),
                ees
        );

        eventRepository.save(domain);
        return mapper.toDetailDTO(domain);
    }
}
