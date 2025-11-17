package at.fhv.Event.application.request.event;

import java.math.BigDecimal;

public class EventEquipmentUpdateRequest {
    private Long id;
    private String name;
    private BigDecimal price;
    private boolean rentable;
    private boolean required;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public boolean isRentable() { return rentable; }
    public void setRentable(boolean rentable) { this.rentable = rentable; }

    public boolean isRequired() { return required; }
    public void setRequired(boolean required) { this.required = required; }
}

