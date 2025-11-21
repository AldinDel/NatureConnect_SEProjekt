package at.fhv.Event.application.request.booking;

import java.time.LocalDateTime;

public class VoucherRequest {
    public Long id;
    public String code;
    public Integer discountPercent;
    public LocalDateTime validFrom;
    public LocalDateTime validUntil;
    public Integer maxUsage;
    public Integer usedCount;
    public Boolean active;
}
