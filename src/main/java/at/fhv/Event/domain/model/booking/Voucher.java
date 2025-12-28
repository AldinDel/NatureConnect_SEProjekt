package at.fhv.Event.domain.model.booking;

import java.time.LocalDateTime;

public class Voucher {
    private String code;
    private int discountPercent;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Integer maxUsage;
    private int usedCount;

    public Voucher() {
        this.usedCount = 0;
    }

    public boolean isActive() {
        if (maxUsage != null && usedCount >= maxUsage) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        if (validFrom != null && now.isBefore(validFrom)) {
            return false;
        }

        if (validUntil != null && now.isAfter(validUntil)) {
            return false;
        }

        return true;
    }

    public void use() {
        if (!isActive()) {
            throw new IllegalStateException("Voucher is not active");
        }

        this.usedCount++;
    }

    public boolean hasUsagesLeft() {
        if (maxUsage == null) {
            return true;
        }
        return usedCount < maxUsage;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setDiscountPercent(int discountPercent) {
        this.discountPercent = discountPercent;
    }

    public void setUsedCount(int usedCount) {
        this.usedCount = usedCount;
    }

    public Integer getDiscountPercent() {
        return discountPercent;
    }

    public void setDiscountPercent(Integer discountPercent) {
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

    public Integer getMaxUsage() {
        return maxUsage;
    }

    public void setMaxUsage(Integer maxUsage) {
        this.maxUsage = maxUsage;
    }

    public Integer getUsedCount() {
        return usedCount;
    }

    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
}
