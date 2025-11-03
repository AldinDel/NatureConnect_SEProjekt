package at.fhv.Authors.persistence;

import at.fhv.Authors.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, Long> {
}
