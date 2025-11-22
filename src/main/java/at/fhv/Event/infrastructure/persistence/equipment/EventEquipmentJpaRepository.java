package at.fhv.Event.infrastructure.persistence.equipment;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EventEquipmentJpaRepository extends JpaRepository<EventEquipmentEntity, EventEquipmentId> {

    List<EventEquipmentEntity> findByEventId(Long eventId);
    void deleteByEventId(Long eventId);

    //check if any equipment is still used by any event
    boolean existsByEquipment_Id(Long equipmentId);
}
