package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchEventService {

    private final EventRepository eventRepository;

    public SearchEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public List<EventOverviewDTO> getAll() {
        return eventRepository.findAll().stream()
                .map(e -> new EventOverviewDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getCategory(),
                        e.getDate(),
                        e.getStartTime(),
                        e.getEndTime(),
                        e.getLocation(),
                        e.getPrice(),
                        e.getImageUrl()
                ))
                .toList();
    }
}
