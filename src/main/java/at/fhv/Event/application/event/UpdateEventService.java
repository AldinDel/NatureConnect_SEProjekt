package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.UpdateEventRequest;
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
public class UpdateEventService {

    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;
    private final EventMapperDTO mapper;

    public UpdateEventService(EventRepository eventRepository,
                              EquipmentRepository equipmentRepository,
                              EventMapperDTO mapper) {
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EventDetailDTO updateEvent(Long id, UpdateEventRequest req) {
        Event existing = eventRepository.findByIdWithEquipments(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        List<Equipment> required = req.getRequiredEquipmentIds() == null ? List.of()
                : req.getRequiredEquipmentIds().stream()
                .map(eid -> equipmentRepository.findById(eid)
                        .orElseThrow(() -> new RuntimeException("Equipment not found: " + eid)))
                .collect(Collectors.toList());

        List<Equipment> optional = req.getOptionalEquipmentIds() == null ? List.of()
                : req.getOptionalEquipmentIds().stream()
                .map(eid -> equipmentRepository.findById(eid)
                        .orElseThrow(() -> new RuntimeException("Equipment not found: " + eid)))
                .collect(Collectors.toList());

        List<EventEquipment> ees = required.stream().map(eq -> new EventEquipment(eq, true)).collect(Collectors.toList());
        ees.addAll(optional.stream().map(eq -> new EventEquipment(eq, false)).collect(Collectors.toList()));

        Difficulty diff = req.getDifficulty();

        Event updated = new Event(
                existing.getId(),
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

        // preserve status
        updated.setCancelled(existing.getCancelled());

        eventRepository.save(updated);
        return mapper.toDetailDTO(updated);
    }
}
