package at.fhv.Event.domain.model.invoice;

import java.math.BigDecimal;

public class InvoiceLine {

    private String description;
    private int quantity;
    private BigDecimal unitPrice;

    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }
}

