package at.fhv.Event.infrastructure.persistence.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, Long> {

    List<InvoiceJpaEntity> findByBookingId(Long bookingId);
}
