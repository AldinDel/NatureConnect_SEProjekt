package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingEquipmentRepository;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.exception.EquipmentNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class GetBookingEquipmentForInvoiceService {

    private final BookingEquipmentRepository bookingEquipmentRepository;
    private final EquipmentRepository equipmentRepository;

    public GetBookingEquipmentForInvoiceService(
            BookingEquipmentRepository bookingEquipmentRepository,
            EquipmentRepository equipmentRepository) {
        this.bookingEquipmentRepository = bookingEquipmentRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public List<Equipment> getEquipmentUsedSoFar(Long bookingId) {

        List<BookingEquipment> bookingEquipments =
                bookingEquipmentRepository.findNotYetInvoicedByBookingId(bookingId);

        List<Equipment> result = new ArrayList<>();
        for (BookingEquipment bookingEquipment : bookingEquipments) {
            Long equipmentId = bookingEquipment.getEquipmentId();

            Equipment equipment = equipmentRepository.findById(equipmentId)
                    .orElseThrow(() -> new EquipmentNotFoundException(equipmentId));
            result.add(equipment);
        }
        return result;
    }
}
