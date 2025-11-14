package at.fhv.Event.domain.model.equipment;

import jakarta.persistence.Embeddable;
import java.io.Serializable;

@Embeddable
public class EventEquipmentId implements Serializable {
    private Long eventId;
    private Long equipmentId;

    public EventEquipmentId() {}

    public EventEquipmentId(Long eventId, Long equipmentId) {
        this.eventId = eventId;
        this.equipmentId = equipmentId;
    }

    // equals & hashCode Pflicht!
}
