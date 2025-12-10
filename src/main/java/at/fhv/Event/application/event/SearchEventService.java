package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SearchEventService {
    private final EventRepository eventRepository;
    private final EventAccessService accessService;

    public SearchEventService(EventRepository eventRepository,  EventAccessService accessService) {
        this.eventRepository = eventRepository;
        this.accessService = accessService;
    }

    @Transactional(readOnly = true)
    public List<EventOverviewDTO> getAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return eventRepository.findAll().stream()
                .filter(e -> !Boolean.TRUE.equals(e.getCancelled()))
                .map(e -> {
                    String displayOrganizer = accessService.determineDisplayOrganizer(
                            e.getOrganizer());

                    return new EventOverviewDTO(
                            e.getId(),
                            e.getTitle(),
                            e.getDescription(),
                            displayOrganizer,
                            e.getOrganizer(),
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
                    );
                })
                .toList();
    }

}