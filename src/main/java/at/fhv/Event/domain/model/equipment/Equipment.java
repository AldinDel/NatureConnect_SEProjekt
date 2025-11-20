package at.fhv.Event.domain.model.equipment;

import java.math.BigDecimal;

public class Equipment {
    private final Long id;
    private final String name;
    private final BigDecimal unitPrice;
    private final boolean rentable;
    private final Integer stock;

    public Equipment(Long id, String name, BigDecimal unitPrice, boolean rentable, Integer stock) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.rentable = rentable;
        this.stock = stock;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public boolean isRentable() {
        return rentable;
    }

    public Integer getStock() {
        return stock;
    }


}
