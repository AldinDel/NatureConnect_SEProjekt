package at.fhv.Event.domain.model.equipment;

public class EventEquipment {
    private final Equipment equipment;
    private final boolean required;

    public EventEquipment(Equipment equipment, boolean required) {
        this.equipment = equipment;
        this.required = required;
    }

    public Equipment getEquipment() { return equipment; }
    public boolean isRequired() { return required; }
}
