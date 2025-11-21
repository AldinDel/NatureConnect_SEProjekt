package at.fhv.Event.domain.model.payment;

import java.util.List;

public interface PaymentTransactionRepository {
    PaymentTransaction save(PaymentTransaction tx);
    List<PaymentTransaction> findByBookingId(Long bookingId);
}
