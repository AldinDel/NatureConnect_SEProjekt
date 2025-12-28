package at.fhv.Event.domain.model.event;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository {
    Optional<Event> findById(Long id);
    Optional<Event> findByIdWithEquipments(Long id);
    List<Event> findAll();
    Event save(Event event);
    List<Event> findByDate(LocalDate date);
    List<Event> findAllForListView();
    List<Event> findByDateForListView(LocalDate date);

}
