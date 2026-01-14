package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.BookingNotFoundException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
public class CreateFinalInvoiceService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final InvoiceRepository invoiceRepository;

    public CreateFinalInvoiceService(
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.invoiceRepository = invoiceRepository;
    }

    /**
     * Creates a final invoice for a booking that has been paid in full
     * (via Credit Card, PayPal, or Invoice payment method)
     */
    @Transactional
    public Invoice createFinalInvoiceForBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(bookingId));

        Event event = eventRepository.findById(booking.getEventId())
                .orElseThrow(() -> new EventNotFoundException(booking.getEventId()));

        List<InvoiceLine> lines = new ArrayList<>();

        // Add event base price
        BigDecimal eventPrice = event.getPrice().multiply(BigDecimal.valueOf(booking.getSeats()));
        lines.add(new InvoiceLine(
                null,
                "Event: " + event.getTitle() + " (" + booking.getSeats() + " seats)",
                booking.getSeats(),
                event.getPrice()
        ));

        // Add equipment if any
        if (booking.getEquipment() != null && !booking.getEquipment().isEmpty()) {
            for (BookingEquipment equipment : booking.getEquipment()) {
                lines.add(new InvoiceLine(
                        equipment.getEquipmentId(),
                        "Equipment (ID: " + equipment.getEquipmentId() + ")",
                        equipment.getQuantity(),
                        equipment.getPricePerUnit()
                ));
            }
        }

        // Add discount if any
        if (booking.getDiscountAmount() > 0) {
            lines.add(new InvoiceLine(
                    null,
                    "Discount",
                    1,
                    BigDecimal.valueOf(-booking.getDiscountAmount())
            ));
        }

        // Create final invoice
        Invoice invoice = Invoice.createFinal(
                event.getId(),
                booking.getId(),
                lines
        );

        return invoiceRepository.save(invoice);
    }
}
