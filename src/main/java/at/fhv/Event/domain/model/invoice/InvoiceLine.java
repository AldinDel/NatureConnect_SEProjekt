package at.fhv.Event.domain.model.invoice;

import java.math.BigDecimal;

public class InvoiceLine {

    private final String description;
    private final int quantity;
    private final BigDecimal unitPrice;

    public InvoiceLine(String description, int quantity, BigDecimal unitPrice) {
        this.description = description;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public String getDescription() {
        return description;
    }

    public int getQuantity() {
        return quantity;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}
