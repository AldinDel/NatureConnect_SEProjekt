package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class CreateInterimInvoiceService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingEquipmentRepository bookingEquipmentRepository;
    private final EventRepository eventRepository;

    public CreateInterimInvoiceService(
            BookingRepository bookingRepository,
            InvoiceRepository invoiceRepository,
            BookingEquipmentRepository bookingEquipmentRepository,
            EventRepository eventRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingEquipmentRepository = bookingEquipmentRepository;
        this.eventRepository = eventRepository;
    }

    public Invoice createInterimInvoice(Long bookingId, List<Long> equipmentIds) {

        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new RuntimeException("At least one service must be selected");
        }

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new RuntimeException("Booking not found: " + bookingId)
                );

        Event event = eventRepository.findById(booking.getEventId())
                .orElseThrow(() ->
                        new RuntimeException("Event not found: " + booking.getEventId())
                );

        if (event.getDate().isAfter(LocalDate.now())) {
            throw new RuntimeException(
                    "Interim invoices cannot include future services"
            );
        }

        List<BookingEquipmentEntity> bookingEquipments =
                bookingEquipmentRepository.findNotYetInvoicedByBookingId(bookingId)
                        .stream()
                        .filter(be -> equipmentIds.contains(be.getEquipmentId()))
                        .toList();

        if (bookingEquipments.isEmpty()) {
            throw new RuntimeException("Selected services are not valid");
        }

        List<InvoiceLine> lines = bookingEquipments.stream()
                .map(be -> new InvoiceLine(
                        be.getEquipmentId(),
                        "Equipment " + be.getEquipmentId(),
                        be.getQuantity(),
                        BigDecimal.valueOf(be.getUnitPrice())
                ))
                .toList();

        Invoice invoice = Invoice.createInterim(
                event.getId(),
                booking.getId(),
                lines
        );

        return invoiceRepository.save(invoice);
    }
}
