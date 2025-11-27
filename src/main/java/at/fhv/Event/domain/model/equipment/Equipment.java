package at.fhv.Event.domain.model.equipment;

import java.math.BigDecimal;

public class Equipment {
    private Long id;
    private String name;
    private BigDecimal unitPrice;
    private boolean rentable;
    private Integer stock;

    public Equipment(Long id, String name, BigDecimal unitPrice, boolean rentable, Integer stock) {
        this.id = id;
        this.name = name;
        this.unitPrice = unitPrice;
        this.rentable = rentable;
        this.stock = stock;
    }

    public void reduceStock(int quantity) {
        if (quantity > this.stock) {
            throw new IllegalStateException(
                    String.format("Can't reduce stock: requested %d, available%d", quantity, this.stock)
            );
        } this.stock = this.stock - quantity;
    }

    public boolean hasEnoughStock(int quantity) {
        return this.stock>=quantity;
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

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public void setRentable(boolean rentable) {
        this.rentable = rentable;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
