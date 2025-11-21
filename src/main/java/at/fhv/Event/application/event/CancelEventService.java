package at.fhv.Event.application.event;

import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CancelEventService {

    private final EventJpaRepository eventJpaRepository;
    private final EventMapperDTO dtoMapper;

    public CancelEventService(EventJpaRepository eventJpaRepository, EventMapperDTO dtoMapper) {
        this.eventJpaRepository = eventJpaRepository;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public EventDetailDTO cancel(Long id) {
        EventEntity entity = eventJpaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        entity.setCancelled(true);

        EventEntity saved = eventJpaRepository.save(entity);
        return null;
    }
}