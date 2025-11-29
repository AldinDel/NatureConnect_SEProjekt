package at.fhv.Event.domain.model.booking;

public class BookingEquipment {

    private Long id;
    private Long equipmentId;
    private int quantity;
    private double unitPrice;
    private double totalPrice;

    public BookingEquipment() {
    }

    public BookingEquipment(Long equipmentId, int quantity, double unitPrice) {
        if (equipmentId == null) {
            throw new IllegalArgumentException("equipmentId must not be null");
        }
        if (quantity < 1) {
            throw new IllegalArgumentException("Quantity must be >= 1");
        }
        if (unitPrice < 0) {
            throw new IllegalArgumentException("Unit price must be >= 0");
        }
        this.equipmentId = equipmentId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        recalculateTotal();
    }

    public void recalculateTotal() {
        this.totalPrice = unitPrice * quantity;
    }

    public void changeQuantity(int newQuantity) {
        if (newQuantity < 1) throw new IllegalArgumentException("Quantity must be >= 1");
        this.quantity = newQuantity;
        recalculateTotal();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getTotalPrice() {
        return unitPrice * quantity;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
}
