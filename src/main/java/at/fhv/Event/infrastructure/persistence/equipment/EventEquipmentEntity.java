package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "event_equipment")
public class EventEquipmentEntity {

    @EmbeddedId
    private EventEquipmentId id = new EventEquipmentId();

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private EquipmentEntity equipment;

    private boolean required;

    public EventEquipmentEntity() {}

    public EventEquipmentEntity(EventEntity event, EquipmentEntity equipment, boolean required) {
        this.event = event;
        this.equipment = equipment;
        this.required = required;
        this.id = new EventEquipmentId(event.getId(), equipment.getId());
    }

    public EventEquipmentId getId() { return id; }
    public EventEntity getEvent() { return event; }
    public EquipmentEntity getEquipment() { return equipment; }
    public boolean isRequired() { return required; }

    public void setId(EventEquipmentId id) {
        this.id = id;
    }

    public void setEvent(EventEntity event) {
        this.event = event;
        if (this.id == null) this.id = new EventEquipmentId();
        this.id.setEventId(event.getId());
    }

    public void setEquipment(EquipmentEntity equipment) {
        this.equipment = equipment;
        if (this.id == null) this.id = new EventEquipmentId();
        this.id.setEquipmentId(equipment.getId());
    }

    public void setRequired(boolean required) {
        this.required = required;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventEquipmentEntity that = (EventEquipmentEntity) o;
        // gleiche Event + gleiche Equipment = gleiches EventEquipment
        return java.util.Objects.equals(event != null ? event.getId() : null, that.event != null ? that.event.getId() : null)
                && java.util.Objects.equals(equipment != null ? equipment.getId() : null, that.equipment != null ? that.equipment.getId() : null);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(
                event != null ? event.getId() : null,
                equipment != null ? equipment.getId() : null
        );
    }

}
