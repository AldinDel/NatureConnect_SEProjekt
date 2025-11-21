package at.fhv.Event.infrastructure.persistence.payment;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "voucher", schema = "nature_connect")
public class VoucherEntity {
    @Id
    @Column(name="code", nullable = false, unique=true)
    private String code;

    @Column(name="discount_percent", nullable = false)
    private int discountPercent;

    @Column(name="valid_from")
    private LocalDateTime validFrom;

    @Column(name="valid_until")
    private LocalDateTime validUntil;

    @Column(name="max_usage")
    private Integer maxUsage;

    @Column(name="used_count", nullable = false)
    private int usedCount;

    @Transient
    private boolean active;

    public VoucherEntity() {}

    public boolean isActive() {
        LocalDateTime now = LocalDateTime.now();

        boolean timeValid =
                (validFrom == null || now.isAfter(validFrom)) &&
                        (validUntil == null || now.isBefore(validUntil));

        boolean usageValid =
                (maxUsage == null || usedCount < maxUsage);

        return timeValid && usageValid;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public void setMaxUsage(Integer maxUsage) {
        this.maxUsage = maxUsage;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Integer getMaxUsage() {
        return maxUsage;
    }


    public int getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }


}
