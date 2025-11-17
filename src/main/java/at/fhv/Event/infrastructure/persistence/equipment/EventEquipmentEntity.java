package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "event_equipment")
public class EventEquipmentEntity {

    @EmbeddedId
    private EventEquipmentId id = new EventEquipmentId();

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private EquipmentEntity equipment;

    private boolean required;

    public EventEquipmentEntity() {}

    public EventEquipmentEntity(EventEntity event, EquipmentEntity equipment, boolean required) {
        this.event = event;
        this.equipment = equipment;
        this.required = required;
    }

    public EventEquipmentId getId() { return id; }
    public EventEntity getEvent() { return event; }
    public EquipmentEntity getEquipment() { return equipment; }
    public boolean isRequired() { return required; }

    public void setEvent(EventEntity event) { this.event = event; }
    public void setEquipment(EquipmentEntity equipment) { this.equipment = equipment; }
    public void setRequired(boolean required) { this.required = required; }
}
