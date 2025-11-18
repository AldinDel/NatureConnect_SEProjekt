package at.fhv.Event.rest.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.rest.mapper.booking.BookingResponseMapper;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin // allow frontend connections
public class BookingRestController {

    private final BookEventService bookEventService;
    private final BookingResponseMapper responseMapper;

    public BookingRestController(
            BookEventService bookEventService,
            BookingResponseMapper responseMapper
    ) {
        this.bookEventService = bookEventService;
        this.responseMapper = responseMapper;
    }

    @PostMapping
    public BookingDTO create(@RequestBody CreateBookingRequest request) {
        return bookEventService.bookEvent(request);
    }

    @GetMapping("/{id}")
    public BookingDTO getById(@PathVariable Long id) {
        var booking = bookEventService.getById(id);
        return responseMapper.toDTO(booking);
    }
}
