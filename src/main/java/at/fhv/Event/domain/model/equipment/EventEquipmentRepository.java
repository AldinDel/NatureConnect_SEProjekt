package at.fhv.Event.domain.model.equipment;

import java.util.List;

public interface EventEquipmentRepository {
    List<Equipment> findEquipmentByEventId(Long eventId);
}
