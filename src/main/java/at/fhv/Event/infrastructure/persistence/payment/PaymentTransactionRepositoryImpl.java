package at.fhv.Event.infrastructure.persistence.payment;

import at.fhv.Event.domain.model.payment.PaymentTransaction;
import at.fhv.Event.domain.model.payment.PaymentTransactionRepository;
import at.fhv.Event.infrastructure.mapper.PaymentTransactionMapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PaymentTransactionRepositoryImpl implements PaymentTransactionRepository {

    private final PaymentTransactionJpaRepository jpa;
    private final PaymentTransactionMapper mapper;

    public PaymentTransactionRepositoryImpl(
            PaymentTransactionJpaRepository jpa,
            PaymentTransactionMapper mapper
    ) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public PaymentTransaction save(PaymentTransaction tx) {
        var entity = mapper.toEntity(tx);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<PaymentTransaction> findByBookingId(Long bookingId) {
        return jpa.findByBookingId(bookingId).stream()
                .map(mapper::toDomain)
                .toList();
    }
}
