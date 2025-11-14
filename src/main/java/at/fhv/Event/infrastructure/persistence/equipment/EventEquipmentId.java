package at.fhv.Event.infrastructure.persistence.equipment;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventEquipmentId implements Serializable {
    private Long eventId;
    private Long equipmentId;

    public EventEquipmentId() {}
    public EventEquipmentId(Long eventId, Long equipmentId) {
        this.eventId = eventId;
        this.equipmentId = equipmentId;
    }

    public Long getEventId() { return eventId; }
    public Long getEquipmentId() { return equipmentId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventEquipmentId)) return false;
        EventEquipmentId that = (EventEquipmentId) o;
        return Objects.equals(eventId, that.eventId) && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, equipmentId);
    }
}
