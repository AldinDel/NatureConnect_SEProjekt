package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.BookingNotFoundException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.exception.InvoiceCreationException;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentJpaRepository;
import org.springframework.stereotype.Service;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class CreateInterimInvoiceService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;
    private final BookingEquipmentJpaRepository bookingEquipmentJpaRepository;
    private final EventRepository eventRepository;
    private final EquipmentRepository equipmentRepository;

    public CreateInterimInvoiceService(
            BookingRepository bookingRepository,
            InvoiceRepository invoiceRepository,
            BookingEquipmentJpaRepository bookingEquipmentJpaRepository,
            EventRepository eventRepository, EquipmentRepository equipmentRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
        this.bookingEquipmentJpaRepository = bookingEquipmentJpaRepository;
        this.eventRepository = eventRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public Invoice createInterimInvoice(
            Long bookingId,
            List<Long> equipmentIds,
            boolean includeEventPrice
    ) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() ->
                        new BookingNotFoundException(bookingId)
                );

        if (!booking.isBillingReady()) {
            throw new InvoiceCreationException(
                    bookingId, "Billing is not allowed before checkout is completed"
            );
        }

        Event event = eventRepository.findById(booking.getEventId())
                .orElseThrow(() ->
                        new EventNotFoundException(booking.getEventId())
                );

        if (event.getDate().isAfter(LocalDate.now())) {
            throw new InvoiceCreationException(
                    bookingId, "Interim invoices cannot include future services"
            );
        }

        List<InvoiceLine> lines = new ArrayList<>();

        if (includeEventPrice) {
            boolean alreadyInvoiced =
                    invoiceRepository.existsEventPriceForBooking(bookingId);

            if (alreadyInvoiced) {
                throw new InvoiceCreationException(
                        bookingId,
                        "Event base price has already been invoiced"
                );
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
                    bookingEquipmentJpaRepository.findByBooking_IdAndInvoicedFalse(bookingId)
                            .stream()
                            .filter(be -> equipmentIds.contains(be.getEquipmentId()))
                            .toList();

            bookingEquipments.forEach(be -> {
                Equipment equipment = equipmentRepository.findById(be.getEquipmentId())
                        .orElseThrow(() ->
                                new InvoiceCreationException(
                                        bookingId,
                                        "Equipment not found: " + be.getEquipmentId()
                                )
                        );

                String description = equipment.getName();
                if (equipment.isRentable()) {
                    description += " (Rental)";
                }

                lines.add(
                        new InvoiceLine(
                                be.getEquipmentId(),
                                description,
                                be.getQuantity(),
                                be.getPricePerUnit()
                        )
                );
            });
        }

        if (lines.isEmpty()) {
            throw new InvoiceCreationException(bookingId, "At least one service must be selected");
        }

        Invoice invoice = Invoice.createInterim(
                event.getId(),
                booking.getId(),
                lines
        );

        Invoice savedInvoice = invoiceRepository.save(invoice);

        savedInvoice.getLines().forEach(line -> {
            if (line.getEquipmentId() != null) {
                bookingEquipmentJpaRepository.markAsInvoiced(
                        bookingId,
                        line.getEquipmentId()
                );
            }
        });

        return savedInvoice;

    }
}
