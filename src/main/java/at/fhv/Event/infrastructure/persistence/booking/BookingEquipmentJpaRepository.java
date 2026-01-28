package at.fhv.Event.infrastructure.persistence.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BookingEquipmentJpaRepository
        extends JpaRepository<BookingEquipmentEntity, Long> {

    @Modifying
    @Query("""
    update BookingEquipmentEntity be
    set be.invoiced = true
    where be.booking.id = :bookingId
      and be.equipmentId = :equipmentId
    """)
    void markAsInvoiced(
            @Param("bookingId") Long bookingId,
            @Param("equipmentId") Long equipmentId
    );


    List<BookingEquipmentEntity> findNotYetInvoicedByBookingId(
            @Param("bookingId") Long bookingId
    );

    List<BookingEquipmentEntity> findByBooking_Id(Long bookingId);
    List<BookingEquipmentEntity> findByBooking_IdAndInvoicedFalse(Long bookingId);
    boolean existsByBookingId(Long bookingId);

}
