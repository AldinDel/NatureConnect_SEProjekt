package at.fhv.Event.presentation.rest.request;

public class WalkInEquipmentRequest {

    private Long equipmentId;
    private int quantity;

    public Long getEquipmentId() {
        return equipmentId;
    }

    public void setEquipmentId(Long equipmentId) {
        this.equipmentId = equipmentId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
