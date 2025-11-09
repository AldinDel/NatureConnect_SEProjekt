package at.fhv.Event.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", insertable = false, updatable = false)
    @JsonBackReference
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "equipment_id", insertable = false, updatable = false)
    private Equipment equipment;

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

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }
}
