package at.fhv.Event.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "event_equipment", schema = "nature_connect")
public class EventEquipment {

    @EmbeddedId
    private EventEquipmentId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("eventId")
    @JoinColumn(name = "event_id")
    @JsonBackReference
    private Event event;

    @ManyToOne(fetch = FetchType.EAGER)
    @MapsId("equipmentId")
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @Column(nullable = false)
    private boolean required = false;

    public EventEquipment() {}

    public EventEquipment(Long eventId, Long equipmentId, boolean required) {
        this.id = new EventEquipmentId(eventId, equipmentId);
        this.required = required;
    }

    public EventEquipmentId getId() { return id; }

    public Event getEvent() { return event; }
    public void setEvent(Event event) { this.event = event; }

    public Equipment getEquipment() { return equipment; }
    public void setEquipment(Equipment equipment) { this.equipment = equipment; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}
