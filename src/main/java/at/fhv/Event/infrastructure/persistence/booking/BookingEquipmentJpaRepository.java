package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingEquipmentJpaRepository
        extends JpaRepository<BookingEquipmentEntity, Long> {

    @Query("""
        select be
        from BookingEquipmentEntity be
        where be.booking.id = :bookingId
          and be.equipmentId not in (
              select ii.equipmentId
              from InvoiceItemEntity ii
              where ii.invoice.bookingId = :bookingId
          )
    """)
    List<BookingEquipmentEntity> findNotYetInvoicedByBookingId(
            @Param("bookingId") Long bookingId
    );

    List<BookingEquipmentEntity> findByBooking_Id(Long bookingId);
    List<BookingEquipmentEntity> findByBooking_IdAndInvoicedFalse(Long bookingId);
}
