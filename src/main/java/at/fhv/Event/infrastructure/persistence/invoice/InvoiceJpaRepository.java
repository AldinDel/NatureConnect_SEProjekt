package at.fhv.Event.infrastructure.persistence.invoice;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface InvoiceJpaRepository extends JpaRepository<InvoiceJpaEntity, Long> {

    List<InvoiceJpaEntity> findByBookingId(Long bookingId);

    List<InvoiceJpaEntity> findByEventId(Long eventId);

    @Modifying
    @Transactional
    @Query("""
        UPDATE InvoiceJpaEntity i
        SET i.status = 'FINAL'
        WHERE i.id = :invoiceId
          AND i.status = 'INTERIM'
    """)
    void finalizeInvoice(@Param("invoiceId") Long invoiceId);
}

