package at.fhv.Event.presentation.rest.webhook;


import at.fhv.Event.application.booking.BookEventService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/payment")
@CrossOrigin(origins = "*")
public class PaymentWebhookRestController {
    private final BookEventService _bookEventService;

    public PaymentWebhookRestController(BookEventService bookEventService) {
        _bookEventService = bookEventService;
    }

    @PostMapping
    public ResponseEntity<Void> handlePayment(@RequestBody PaymentWebhookDTO dto) {
        if ("SUCCESS".equals(dto.status())) {
            _bookEventService.markAsPaid(dto.bookingId());
        } else {
            _bookEventService.markAsFailed(dto.bookingId());
        }
        return ResponseEntity.ok().build();
    }

}
