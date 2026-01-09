package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.exception.PaymentOperationException;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceLine;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class SplitInvoiceService {

    private static final Logger logger = LoggerFactory.getLogger(SplitInvoiceService.class);

    private final BookingRepository bookingRepository;
    private final InvoiceRepository invoiceRepository;

    public SplitInvoiceService(BookingRepository bookingRepository, InvoiceRepository invoiceRepository) {
        this.bookingRepository = bookingRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Transactional
    public void payFiftyPercent(Long bookingId, String userEmail) {
        logger.debug("payFiftyPercent called for booking {}", bookingId);

        Booking booking = getBookingForUser(bookingId, userEmail);
        logger.debug("Booking {} found - Total: {}, Paid: {}, PaymentStatus: {}",
                bookingId, booking.getTotalPrice(), booking.getPaidAmount(), booking.getPaymentStatus());

        if (booking.isFullyPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        double totalPrice = booking.getTotalPrice();
        if (totalPrice <= 0) {
            logger.warn("Booking {} has total price of {}", bookingId, totalPrice);
            throw new IllegalArgumentException("Booking has no amount to pay (total price is 0 or negative)");
        }

        double halfAmount = totalPrice * 0.5;
        double amountToPay = halfAmount - booking.getPaidAmount();
        logger.debug("Amount to pay (50%) for booking {}: {}", bookingId, amountToPay);

        if (amountToPay <= 0) {
            throw new IllegalStateException("50% or more has already been paid");
        }

        // Create Invoice entity for 50% payment
        InvoiceLine paymentLine = new InvoiceLine(
                null,
                "50% Partial Payment",
                1,
                BigDecimal.valueOf(amountToPay)
        );

        Invoice invoice = Invoice.createInterim(
                booking.getEventId(),
                booking.getId(),
                List.of(paymentLine)
        );
        logger.debug("Invoice created for booking {} with total: {}", bookingId, invoice.getTotal());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Invoice {} created for 50% payment of booking {}", savedInvoice.getId(), bookingId);

        if (savedInvoice == null || savedInvoice.getId() == null) {
            throw new RuntimeException("Failed to save invoice");
        }

        // Update booking
        booking.payFiftyPercent();
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking {} updated - New paid amount: {}", bookingId, savedBooking.getPaidAmount());
    }

    @Transactional
    public void paySelectedEquipment(Long bookingId, String userEmail, List<Long> equipmentIds) {
        logger.debug("paySelectedEquipment called for booking {} with equipment IDs: {}", bookingId, equipmentIds);

        Booking booking = getBookingForUser(bookingId, userEmail);
        logger.debug("Booking {} found with {} equipment items",
                bookingId, booking.getEquipment() != null ? booking.getEquipment().size() : 0);

        if (booking.isFullyPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        if (equipmentIds == null || equipmentIds.isEmpty()) {
            throw new IllegalArgumentException("No equipment items selected");
        }

        if (booking.getEquipment() == null || booking.getEquipment().isEmpty()) {
            logger.warn("Booking {} has no equipment items", bookingId);
            throw new IllegalArgumentException("This booking has no equipment items to pay for");
        }

        // Create Invoice lines for selected equipment
        List<InvoiceLine> lines = booking.getEquipment().stream()
                .filter(e -> equipmentIds.contains(e.getEquipmentId()))
                .map(e -> {
                    logger.debug("Adding equipment to invoice - ID: {}, Qty: {}, Price: {}",
                            e.getEquipmentId(), e.getQuantity(), e.getTotalPrice());
                    return new InvoiceLine(
                            e.getEquipmentId(),
                            "Equipment ID " + e.getEquipmentId(),
                            e.getQuantity(),
                            e.getPricePerUnit()
                    );
                })
                .toList();

        if (lines.isEmpty()) {
            logger.warn("No matching equipment found for booking {} with IDs: {}", bookingId, equipmentIds);
            throw new IllegalArgumentException("No valid equipment items found for the selected IDs");
        }

        // Create Invoice entity for equipment payment
        Invoice invoice = Invoice.createInterim(
                booking.getEventId(),
                booking.getId(),
                lines
        );
        logger.debug("Invoice created for booking {} with {} lines, total: {}", bookingId, lines.size(), invoice.getTotal());

        Invoice savedInvoice = invoiceRepository.save(invoice);
        logger.info("Invoice {} created for equipment payment of booking {}", savedInvoice.getId(), bookingId);

        // Update booking
        booking.payEquipmentItems(equipmentIds);
        Booking savedBooking = bookingRepository.save(booking);
        logger.info("Booking {} updated - New paid amount: {}", bookingId, savedBooking.getPaidAmount());
    }

    @Transactional
    public void payRemainingAmount(Long bookingId, String userEmail) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.isFullyPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        double remaining = booking.getRemainingAmount();
        if (remaining > 0) {
            // Create Invoice entity for remaining payment
            InvoiceLine paymentLine = new InvoiceLine(
                    null,
                    "Remaining Payment",
                    1,
                    BigDecimal.valueOf(remaining)
            );

            Invoice invoice = Invoice.createInterim(
                    booking.getEventId(),
                    booking.getId(),
                    List.of(paymentLine)
            );

            invoiceRepository.save(invoice);

            // Update booking
            booking.makePartialPayment(remaining);
            bookingRepository.save(booking);
        }
    }

    private Booking getBookingForUser(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getBookerEmail().equalsIgnoreCase(userEmail)) {
            throw new PaymentOperationException(bookingId, "You can only manage your own bookings");
        }

        return booking;
    }
}
