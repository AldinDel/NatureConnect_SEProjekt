package at.fhv.Event.infrastructure.persistence.payment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentTransactionJpaRepository
        extends JpaRepository<PaymentTransactionEntity, Long> {

    List<PaymentTransactionEntity> findByBookingId(Long bookingId);
}