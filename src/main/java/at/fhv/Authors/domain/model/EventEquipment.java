package at.fhv.Authors.domain.model;

import jakarta.persistence.*;

@Entity
@Table(name = "event_equipment", schema = "nature_connect")
@IdClass(EventEquipmentId.class)
public class EventEquipment {

    @Id
    @Column(name = "event_id")
    private Long eventId;

    @Id
    @Column(name = "equipment_id")
    private Long equipmentId;

    @Column(nullable = false)
    private boolean required = false;

    public EventEquipment() {}

    public EventEquipment(Long eventId, Long equipmentId, boolean required) {
        this.eventId = eventId;
        this.equipmentId = equipmentId;
        this.required = required;
    }

    public Long getEventId() { return eventId; }
    public Long getEquipmentId() { return equipmentId; }
    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
