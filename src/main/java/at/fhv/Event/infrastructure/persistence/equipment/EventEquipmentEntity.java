package at.fhv.Event.infrastructure.persistence.equipment;

import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import jakarta.persistence.*;

@Entity
@Table(name = "event_equipment")
public class EventEquipmentEntity {

    @EmbeddedId
    private EventEquipmentId id;

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

    // Note: when creating new association, do not set id manually; rely on JPA and MapsId
    public EventEquipmentEntity(EventEntity event, EquipmentEntity equipment, boolean required) {
        this.event = event;
        this.equipment = equipment;
        this.required = required;
    }

    public EventEquipmentId getId() { return id; }
    public void setId(EventEquipmentId id) { this.id = id; }
    public EventEntity getEvent() { return event; }
    public void setEvent(EventEntity event) { this.event = event; }
    public EquipmentEntity getEquipment() { return equipment; }
    public void setEquipment(EquipmentEntity equipment) { this.equipment = equipment; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
