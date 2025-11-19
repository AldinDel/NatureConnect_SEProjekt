package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.PaymentTransaction;
import at.fhv.Event.infrastructure.persistence.booking.PaymentTransactionEntity;
import org.springframework.stereotype.Component;
@Component
public class PaymentTransactionMapper {

    public PaymentTransactionEntity toEntity(PaymentTransaction domain) {
        var entity = new PaymentTransactionEntity();

        if (domain.getId() != null) {
            entity.setId(domain.getId());
        }

        entity.setBookingId(domain.getBookingId());
        entity.setMethod(domain.getMethod());
        entity.setAmount(domain.getAmount());
        entity.setStatus(domain.getStatus());
        entity.setCreatedAt(domain.getCreatedAt());

        return entity;
    }

    public PaymentTransaction toDomain(PaymentTransactionEntity entity) {
        var domain = new PaymentTransaction(
                entity.getBookingId(),
                entity.getMethod(),
                entity.getAmount()
        );

        domain.setId(entity.getId());
        domain.setStatus(entity.getStatus());
        domain.setCreatedAt(entity.getCreatedAt());

        return domain;
    }
}