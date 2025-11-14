package at.fhv.Event.domain.model.event;

import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Event> findById(Long id);
    Optional<Event> findByIdWithEquipments(Long id);
    List<Event> findAll();
    void save(Event event);
}
