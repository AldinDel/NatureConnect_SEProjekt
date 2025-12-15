package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.ParticipantStatus;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentRepository;
import at.fhv.Event.infrastructure.persistence.booking.JpaBookingParticipantRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateInterimInvoiceService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingEquipmentRepository bookingEquipmentRepository;
    private final EventRepository eventRepository;
    private final JpaBookingParticipantRepository bookingParticipantRepository;

    public CreateInterimInvoiceService(
            BookingRepository bookingRepository,
            InvoiceRepository invoiceRepository,
            BookingEquipmentRepository bookingEquipmentRepository,
            EventRepository eventRepository,
            JpaBookingParticipantRepository bookingParticipantRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingEquipmentRepository = bookingEquipmentRepository;
        this.eventRepository = eventRepository;
        this.bookingParticipantRepository = bookingParticipantRepository;
    }

    public Invoice createInterimInvoice(
            Long bookingId,
            List<Long> equipmentIds,
            boolean includeEventPrice
    ) {

        boolean checkedOut =
                bookingParticipantRepository.existsByBooking_IdAndCheckOutStatus(
                        bookingId,
                        ParticipantStatus.CHECKED_OUT
                );

        if (checkedOut) {
            throw new RuntimeException(
                    "Interim invoices cannot be issued after checkout"
            );
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

        List<InvoiceLine> lines = new ArrayList<>();

        if (includeEventPrice) {
            boolean alreadyInvoiced =
                    invoiceRepository.existsEventPriceForBooking(bookingId);

            if (alreadyInvoiced) {
                throw new RuntimeException("Event base price already invoiced");
            }

            lines.add(
                    new InvoiceLine(
                            null,
                            "Event base price",
                            1,
                            event.getPrice()
                    )
            );
        }

        if (equipmentIds != null && !equipmentIds.isEmpty()) {
            List<BookingEquipmentEntity> bookingEquipments =
                    bookingEquipmentRepository.findNotYetInvoicedByBookingId(bookingId)
                            .stream()
                            .filter(be -> equipmentIds.contains(be.getEquipmentId()))
                            .toList();

            bookingEquipments.forEach(be ->
                    lines.add(
                            new InvoiceLine(
                                    be.getEquipmentId(),
                                    "Equipment " + be.getEquipmentId(),
                                    be.getQuantity(),
                                    BigDecimal.valueOf(be.getUnitPrice())
                            )
                    )
            );
        }

        if (lines.isEmpty()) {
            throw new RuntimeException("At least one service must be selected");
        }

        Invoice invoice = Invoice.createInterim(
                event.getId(),
                booking.getId(),
                lines
        );

        return invoiceRepository.save(invoice);
    }
}
