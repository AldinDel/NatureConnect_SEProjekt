package at.fhv.Event.application.invoice;

import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CreateInterimInvoiceService {

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;

    public CreateInterimInvoiceService(
            BookingRepository bookingRepository,
            InvoiceRepository invoiceRepository
    ) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
    }

    public Invoice createInterimInvoice(
            Long bookingId,
            List<InvoiceLine> lines
    ) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // if (booking.isCheckedOut()) {
//     throw new RuntimeException("Cannot create interim invoice after final checkout");
// } // TODO: replace with booking.isCheckedOut() once checkout is implemented


        Invoice invoice = Invoice.createInterim(bookingId, lines);
        return invoiceRepository.save(invoice);
    }
}
