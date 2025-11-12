package at.fhv.Event.application;

import at.fhv.Event.domain.model.Event;
import at.fhv.Event.dto.EquipmentDTO;
import at.fhv.Event.dto.EventDetailDTO;
import at.fhv.Event.persistence.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEventDetailsService {

    private final EventRepository eventRepository;

    public GetEventDetailsService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public EventDetailDTO getEventDetails(Long eventId) {
        Event event = eventRepository.findByIdWithEquipments(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found"));

        List<EquipmentDTO> equipmentList = event.getEventEquipments() != null
                ? event.getEventEquipments().stream()
                .filter(eq -> eq.getEquipment() != null)
                .map(eq -> new EquipmentDTO(
                        eq.getEquipment().getId(),
                        eq.getEquipment().getName(),
                        eq.getEquipment().getUnitPrice(),
                        eq.getEquipment().isRentable(),
                        eq.isRequired()
                ))
                .toList()
                : List.of();

        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getOrganizer(),
                event.getCategory(),
                event.getDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getDifficulty().toString(),
                event.getMinParticipants(),
                event.getMaxParticipants(),
                event.getPrice(),
                event.getImageUrl(),
                event.getAudience(),
                equipmentList
        );
    }
}
