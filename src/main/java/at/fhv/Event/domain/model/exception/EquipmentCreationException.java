package at.fhv.Event.domain.model.exception;

public class EquipmentCreationException extends DomainException {
    private final String equipmentName;
    private final String reason;

    public EquipmentCreationException(String equipmentName, String reason) {
        super("EQUIPMENT_003");
        this.equipmentName = equipmentName;
        this.reason = reason;
    }

    public String getEquipmentName() {
        return equipmentName;
    }

    public String getReason() {
        return reason;
    }
}