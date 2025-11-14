package at.fhv.Event.application.request.equipment;

import java.math.BigDecimal;

public class UpdateEquipmentRequest {
    private String name;
    private BigDecimal unitPrice;
    private boolean rentable;

    public UpdateEquipmentRequest() {}

    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public boolean isRentable() { return rentable; }

    public void setName(String name) { this.name = name; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setRentable(boolean rentable) { this.rentable = rentable; }
}
