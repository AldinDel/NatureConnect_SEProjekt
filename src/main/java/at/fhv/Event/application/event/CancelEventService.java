package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.exception.EventAlreadyCancelledException;
import at.fhv.Event.domain.model.exception.EventDateInPastException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class CancelEventService {

    private final EventJpaRepository eventJpaRepository;
    private final EventMapperDTO dtoMapper;

    public CancelEventService(EventJpaRepository eventJpaRepository, EventMapperDTO dtoMapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public void cancel(Long id) {
        EventEntity entity = eventJpaRepository.findById(id)
                .orElseThrow(() -> new EventNotFoundException(id));

        // Check if event is already cancelled
        if (Boolean.TRUE.equals(entity.getCancelled())) {
            throw new EventAlreadyCancelledException(id);
        }

        // Check if event date is in past
        LocalDateTime eventStart = LocalDateTime.of(entity.getDate(), entity.getStartTime());
        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new EventDateInPastException(id, entity.getDate());
        }

        entity.setCancelled(true);
        eventJpaRepository.save(entity);
    }
}