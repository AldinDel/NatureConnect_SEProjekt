package at.fhv.Authors.persistence;

import at.fhv.Authors.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Event> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String category, String title, String description);
}
