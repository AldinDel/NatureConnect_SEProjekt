package at.fhv.Event.domain.model.equipment;

public class EquipmentSelection {
    private boolean selected;
    private int quantity;

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
