package at.fhv.Event.domain.model.exception;

public class InsufficientStockException extends DomainException {
    private final Long _equipmentId;
    private final String _equipmentName;
    private final int _requestedQuantity;
    private final int _availableQuantity;

    public InsufficientStockException(Long equipmentId, String equipmentName, int requestedQuantity, int availableQuantity) {
        super("EQUIPMENT_002");
        _equipmentId = equipmentId;
        _equipmentName = equipmentName;
        _requestedQuantity = requestedQuantity;
        _availableQuantity = availableQuantity;
    }

    public Long getEquipmentId() {
        return _equipmentId;
    }
    public String getEquipmentName() {
        return _equipmentName;
    }
    public int getRequestedQuantity() {
        return _requestedQuantity;
    }
    public int getAvailableQuantity() {
        return _availableQuantity;
    }
}
