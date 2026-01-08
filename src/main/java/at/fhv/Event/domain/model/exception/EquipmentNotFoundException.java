package at.fhv.Event.domain.model.exception;

public class EquipmentNotFoundException extends DomainException {
    private final Long equipmentId;

    public EquipmentNotFoundException(Long equipmentId) {
        super("EQUIPMENT_001");
        this.equipmentId = equipmentId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }
}