package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EventEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEquipmentForEventService {

    private final EventEquipmentRepository eventEquipmentRepository;

    public GetEquipmentForEventService(EventEquipmentRepository eventEquipmentRepository) {
        this.eventEquipmentRepository = eventEquipmentRepository;
    }

    public List<Equipment> getForEvent(Long eventId) {
        return eventEquipmentRepository.findEquipmentByEventId(eventId);
    }
}
