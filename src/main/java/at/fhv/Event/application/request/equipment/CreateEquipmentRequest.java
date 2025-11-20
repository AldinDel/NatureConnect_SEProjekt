package at.fhv.Event.application.request.equipment;

import java.math.BigDecimal;

public class CreateEquipmentRequest {
    private String name;
    private BigDecimal unitPrice;
    private boolean rentable;
    private Integer stock;

    public CreateEquipmentRequest() {}

    public String getName() { return name; }
    public BigDecimal getUnitPrice() { return unitPrice; }
    public boolean isRentable() { return rentable; }
    public Integer getStock() { return stock; }

    public void setName(String name) { this.name = name; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }
    public void setRentable(boolean rentable) { this.rentable = rentable; }
    public void setStock(Integer stock) { this.stock = stock; }
}
