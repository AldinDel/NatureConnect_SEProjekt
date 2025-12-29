package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingRestController {

    private final BookEventService _bookEventService;

    public BookingRestController(BookEventService bookEventService) {
        _bookEventService = bookEventService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO> createBooking(@RequestBody CreateBookingRequest request) {
        BookingDTO booking = _bookEventService.bookEvent(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(booking);
    }

    @GetMapping("/event/{id}")
    public ResponseEntity<BookingDTO> getBookingById(@PathVariable Long id) {
        BookingDTO booking = _bookEventService.getDTOById(id);
        return ResponseEntity.ok(booking);
    }

    @PostMapping("/{id}/payment")
    public ResponseEntity<BookingDTO> updatePaymentMethod(
            @PathVariable Long id,
            @RequestParam String paymentMethod) {

        BookingDTO booking = _bookEventService.updatePaymentMethod(id, paymentMethod);
        return ResponseEntity.ok(booking);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<String>> getPaymentMethods() {
        List<String> paymentMethods = new ArrayList<>();

        for (PaymentMethod method : PaymentMethod.values()) {
            paymentMethods.add(method.name());
        }

        return ResponseEntity.ok(paymentMethods);
    }
}