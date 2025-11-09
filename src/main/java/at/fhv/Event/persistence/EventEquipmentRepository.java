package at.fhv.Event.persistence;

import at.fhv.Event.domain.model.EventEquipment;
import at.fhv.Event.domain.model.EventEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventEquipmentRepository extends JpaRepository<EventEquipment, EventEquipmentId> {
    List<EventEquipment> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);
}
