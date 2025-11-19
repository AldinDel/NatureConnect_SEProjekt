package at.fhv.Event.rest.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingRestController {

    private final BookEventService bookEventService;

    public BookingRestController(BookEventService bookEventService) {
        this.bookEventService = bookEventService;
    }

    @PostMapping
    public BookingDTO create(@RequestBody CreateBookingRequest request) {
        return bookEventService.bookEvent(request);
    }

    @GetMapping("/{id}")
    public BookingDTO getById(@PathVariable Long id) {
        return bookEventService.getDTOById(id);
    }
}
