package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;

@Service
public class GetEventDetailsService {

    private final EventRepository eventRepository;
    private final EventMapperDTO mapper;

    public GetEventDetailsService(EventRepository eventRepository, EventMapperDTO mapper) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    public EventDetailDTO getEventDetails(Long eventId) {
        System.out.println("=== DEBUG: GetEventDetailsService called with ID: " + eventId + " ===");

        // Teste erst mit normalem findById
        System.out.println("=== DEBUG: GetEventDetailsService called with ID: " + eventId + " ===");
        var eventOptional = eventRepository.findByIdWithEquipments(eventId);
        System.out.println("DEBUG: findByIdWithEquipments result: " + (eventOptional.isPresent() ? "FOUND" : "NOT FOUND"));

        if (eventOptional.isEmpty()) {
            System.err.println("ERROR: Event with ID " + eventId + " does not exist!");
            throw new RuntimeException("Event not found: " + eventId);
        }

        var event = eventOptional.get();
        System.out.println("DEBUG: Domain event loaded: " + event.getTitle());
        System.out.println("DEBUG: EventEquipments size(): " + event.getEventEquipments().size());

        return mapper.toDetailDTO(event);
    }
}