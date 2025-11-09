package at.fhv.Event.domain.model;

import java.io.Serializable;
import java.util.Objects;

public class EventEquipmentId implements Serializable {

    private Long eventId;
    private Long equipmentId;

    public EventEquipmentId() {}

    public EventEquipmentId(Long eventId, Long equipmentId) {
        this.eventId = eventId;
        this.equipmentId = equipmentId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof EventEquipmentId that)) return false;
        return Objects.equals(eventId, that.eventId)
                && Objects.equals(equipmentId, that.equipmentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventId, equipmentId);
    }
}
