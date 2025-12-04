package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventAlreadyCancelledException;
import at.fhv.Event.domain.model.exception.EventDateInPastException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelEventService {
    private final EventRepository _eventRepository;

    public CancelEventService(EventRepository eventRepository) {
        _eventRepository = eventRepository;
    }

    @Transactional
    public void cancel(Long id) {
        Event event = _eventRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new EventAlreadyCancelledException(id);
        }

        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new EventDateInPastException(id, event.getDate());
        }

        event.setCancelled(true);
        _eventRepository.save(event);
    }
}