package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelEventService {

    private final EventRepository eventRepository;
    private final EventMapperDTO mapper;

    public CancelEventService(EventRepository eventRepository, EventMapperDTO mapper) {
        this.eventRepository = eventRepository;
        this.mapper = mapper;
    }

    @Transactional
    public EventDetailDTO cancel(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        event.setCancelled(true);
        eventRepository.save(event);
        return mapper.toDetailDTO(event);
    }

}
