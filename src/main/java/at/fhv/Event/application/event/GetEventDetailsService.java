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
        var event = eventRepository.findByIdWithEquipments(eventId)
                .orElseThrow(() -> new RuntimeException("Event not found: " + eventId));
        return mapper.toDetailDTO(event);
    }
}
