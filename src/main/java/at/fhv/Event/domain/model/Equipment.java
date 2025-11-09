package at.fhv.Event.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "equipment", schema = "nature_connect")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(nullable = false)
    private boolean rentable = true;

    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice = BigDecimal.ZERO;

    @Column
    private Integer stock;

    @Column(length = 200)
    private String note;

    public Equipment() {}

    public Equipment(String name) {
        this.name = name;
    }

    // --- Getter & Setter ---
    public Long getId() { return id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isRentable() { return rentable; }
    public void setRentable(boolean rentable) { this.rentable = rentable; }

    public BigDecimal getUnitPrice() { return unitPrice; }
    public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    // Kompatible "alias"-Methoden, falls dein Code getPrice()/setPrice() verwendet
    public BigDecimal getPrice() {
        return this.unitPrice;
    }

    public void setPrice(BigDecimal price) {
        this.unitPrice = price;
    }
}
