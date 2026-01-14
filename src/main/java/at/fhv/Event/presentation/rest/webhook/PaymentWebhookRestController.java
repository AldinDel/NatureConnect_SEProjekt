package at.fhv.Event.presentation.rest.webhook;


import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.invoice.CreateFinalInvoiceService;
import at.fhv.Event.domain.model.booking.Booking;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/payment")
@CrossOrigin(origins = "*")
public class PaymentWebhookRestController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentWebhookRestController.class);

    private final BookEventService _bookEventService;
    private final CreateFinalInvoiceService _createFinalInvoiceService;

    public PaymentWebhookRestController(
            BookEventService bookEventService,
            CreateFinalInvoiceService createFinalInvoiceService) {
        _bookEventService = bookEventService;
        _createFinalInvoiceService = createFinalInvoiceService;
    }

    @PostMapping
    public ResponseEntity<Void> handlePayment(@RequestBody PaymentWebhookDTO dto) {
        if ("SUCCESS".equals(dto.status())) {
            logger.info("Payment webhook received SUCCESS for booking {}", dto.bookingId());

            // Mark booking as paid
            _bookEventService.markAsPaid(dto.bookingId());

            // Create final invoice for successful payment
            try {
                Booking booking = _bookEventService.getById(dto.bookingId());
                _createFinalInvoiceService.createFinalInvoiceForBooking(dto.bookingId());
                logger.info("Final invoice created for booking {}", dto.bookingId());
            } catch (Exception e) {
                logger.error("Failed to create final invoice for booking {}: {}",
                        dto.bookingId(), e.getMessage(), e);
                // Don't fail the webhook, payment is already processed
            }
        } else {
            logger.warn("Payment webhook received FAILURE for booking {}", dto.bookingId());
            _bookEventService.markAsFailed(dto.bookingId());
        }
        return ResponseEntity.ok().build();
    }

}
