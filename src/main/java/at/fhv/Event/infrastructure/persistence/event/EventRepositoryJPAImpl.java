package at.fhv.Event.infrastructure.persistence.event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class EventRepositoryJPAImpl implements EventRepository {
    private final EventJpaRepository jpa;
    private final EventMapper mapper;

    public EventRepositoryJPAImpl(EventJpaRepository jpa, EventMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Optional<Event> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Event> findByIdWithEquipments(Long id) {
        return jpa.findByIdWithEquipments(id).map(mapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return jpa.findAll().stream().map(mapper::toDomain).collect(Collectors.toList());
    }

    @Override
    public void save(Event event) {
        var entity = mapper.toEntity(event);
        var saved = jpa.save(entity);
        // If needed, mapping back to domain could be done to update ids.
    }
}
