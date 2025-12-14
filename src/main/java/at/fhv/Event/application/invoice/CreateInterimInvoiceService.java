package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateInterimInvoiceService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final EquipmentRepository equipmentRepository;

    public CreateInterimInvoiceService(
            BookingRepository bookingRepository,
            InvoiceRepository invoiceRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public Invoice createInterimInvoice(Long eventId, List<Long> equipmentIds) {

        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new RuntimeException("At least one service must be selected");
        }

        Booking booking = bookingRepository.findByEventId(eventId)
                .stream()
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException("No booking found for event " + eventId)
                );

        List<Equipment> equipments = equipmentRepository
                .findByIds(equipmentIds)
                .values()
                .stream()
                .toList();

        List<InvoiceLine> lines = equipments.stream()
                .map(e -> new InvoiceLine(
                        e.getName(),
                        1,
                        e.getUnitPrice()
                ))
                .toList();

        Invoice invoice = Invoice.createInterim(
                eventId,
                booking.getId(),
                lines
        );

        return invoiceRepository.save(invoice);
    }

}
