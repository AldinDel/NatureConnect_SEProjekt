package at.fhv.Event.infrastructure.persistence.event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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
        return jpa.findByIdWithEquipments(id).map(mapper::toDomain);
    }

    @Override
    public Optional<Event> findByIdWithEquipments(Long id) {
        return jpa.findByIdWithEquipments(id).map(mapper::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return mapper.toDomainList(jpa.findAllWithEquipmentsAndHikeKeys());
    }


    @Override
    @org.springframework.transaction.annotation.Transactional
    public Event save(Event event) {

        EventEntity entity;

        if (event.getId() == null) {
            entity = mapper.toEntity(event);
        } else {
            entity = jpa.findByIdWithEquipments(event.getId()).orElseThrow();
            mapper.applyToExistingEntity(event, entity);
        }

        var saved = jpa.saveAndFlush(entity);

        var reloaded = jpa.findByIdWithEquipments(saved.getId()).orElseThrow();
        return mapper.toDomain(reloaded);
    }


    @Override
    public List<Event> findByDate(LocalDate date) {
        return mapper.toDomainList(jpa.findByDateWithEquipmentsAndHikeKeys(date));
    }

    @Override
    public List<Event> findAllForListView() {
        return mapper.toDomainList(jpa.findAllWithEquipmentsAndHikeKeys());
    }

    @Override
    public List<Event> findByDateForListView(LocalDate date) {
        return mapper.toDomainList(jpa.findByDateWithEquipmentsAndHikeKeys(date));
    }

    @Override
    public List<Event> findAllByIds(Set<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return List.of();
        }
        return jpa.findAllByIdWithEquipments(ids).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
