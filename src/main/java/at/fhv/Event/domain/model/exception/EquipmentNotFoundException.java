package at.fhv.Event.domain.model.exception;

public class EquipmentNotFoundException extends RuntimeException {
    private final Long equipmentId;

    public EquipmentNotFoundException(Long equipmentId) {
        super("Equipment not found with id: " + equipmentId);
        this.equipmentId = equipmentId;
    }

    public Long getEquipmentId() {
        return equipmentId;
    }
}