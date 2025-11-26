package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchEventService {

    private final EventRepository eventRepository;

    public SearchEventService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    @Transactional(readOnly = true)
    public List<EventOverviewDTO> getAll() {
        return eventRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getCancelled()))
                .map(e -> new EventOverviewDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getOrganizer(), // NEU: Organizer Feld
                        e.getCategory(),
                        e.getDate(),
                        e.getStartTime(),
                        e.getEndTime(),
                        e.getLocation(),
                        e.getDifficulty() != null ? e.getDifficulty().toString() : null,
                        e.getMinParticipants(),
                        e.getMaxParticipants(),
                        e.getPrice(),
                        e.getImageUrl(),
                        e.getAudience() != null ? e.getAudience().toString() : null,
                        e.getCancelled()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EventOverviewDTO> getAllIncludingCancelled() {
        return eventRepository.findAll().stream()
                .map(e -> new EventOverviewDTO(
                        e.getId(),
                        e.getTitle(),
                        e.getDescription(),
                        e.getOrganizer(), // NEU: Organizer Feld
                        e.getCategory(),
                        e.getDate(),
                        e.getStartTime(),
                        e.getEndTime(),
                        e.getLocation(),
                        e.getDifficulty() != null ? e.getDifficulty().toString() : null,
                        e.getMinParticipants(),
                        e.getMaxParticipants(),
                        e.getPrice(),
                        e.getImageUrl(),
                        e.getAudience() != null ? e.getAudience().toString() : null,
                        e.getCancelled()
                ))
                .toList();
    }
}