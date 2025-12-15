package at.fhv.Event.application.booking;

import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetBookingEquipmentForInvoiceService {

    private final BookingEquipmentRepository bookingEquipmentRepository;
    private final EquipmentJpaRepository equipmentRepository;

    public GetBookingEquipmentForInvoiceService(
            BookingEquipmentRepository bookingEquipmentRepository,
            EquipmentJpaRepository equipmentRepository
    ) {
        this.bookingEquipmentRepository = bookingEquipmentRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<EquipmentEntity> getEquipmentUsedSoFar(Long bookingId) {

        List<BookingEquipmentEntity> bookingEquipments =
                bookingEquipmentRepository.findNotYetInvoicedByBookingId(bookingId);

        return bookingEquipments.stream()
                .map(be ->
                        equipmentRepository.findById(be.getEquipmentId())
                                .orElseThrow(() ->
                                        new RuntimeException(
                                                "Equipment not found: " + be.getEquipmentId()
                                        )
                                )
                )
                .toList();
    }
}
