package at.fhv.Event.infrastructure.persistence.invoice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InvoiceItemJpaRepository
        extends JpaRepository<InvoiceItemEntity, Long> {

    List<InvoiceItemEntity> findByInvoice_Id(Long invoiceId);

    List<InvoiceItemEntity> findByEquipmentIdAndInvoice_BookingId(
            Long equipmentId,
            Long bookingId
    );

    boolean existsByInvoice_BookingIdAndEquipmentIdIsNull(Long bookingId);
}
