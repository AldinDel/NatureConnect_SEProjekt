package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.Voucher;
import at.fhv.Event.infrastructure.persistence.payment.VoucherEntity;
import org.springframework.stereotype.Component;

@Component
public class VoucherMapper {
    public Voucher toDomain(VoucherEntity entity) {
        if (entity == null) {
            return null;
        }

        Voucher domain = new Voucher();
        domain.setCode(entity.getCode());
        domain.setDiscountPercent(entity.getDiscountPercent());
        domain.setValidFrom(entity.getValidFrom());
        domain.setValidUntil(entity.getValidUntil());
        domain.setMaxUsage(entity.getMaxUsage());
        domain.setUsedCount(entity.getUsedCount());

        return domain;
    }

    public VoucherEntity toEntity(Voucher domain) {
        if (domain == null) {
            return null;
        }

        VoucherEntity entity = new VoucherEntity();
        entity.setCode(domain.getCode());
        entity.setDiscountPercent(domain.getDiscountPercent());
        entity.setValidFrom(domain.getValidFrom());
        entity.setValidUntil(domain.getValidUntil());
        entity.setMaxUsage(domain.getMaxUsage());
        entity.setUsedCount(domain.getUsedCount());

        return entity;
    }
}
