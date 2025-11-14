package at.fhv.Event.ui;

import at.fhv.Event.application.booking.GetAllBookingsService;
import at.fhv.Event.rest.response.booking.BookingDTO;
import at.fhv.Event.domain.Events;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuthorTemplateProvider {

    private final GetAllBookingsService bookingService;

    public AuthorTemplateProvider(GetAllBookingsService bookingService) {
        this.bookingService = bookingService;
    }

    @GetMapping("/authors")
    public ModelAndView getAuthorTemplate() {

        // Testdaten wie vorher
        List<Events> events = Arrays.asList(
                new Events("Ralph", "Hoch"),
                new Events("FH", "Vorarlberg")
        );

        // Optional: echte Bookings aus Application Layer
        List<BookingDTO> bookings = bookingService.getAllBookings();

        ModelAndView mv = new ModelAndView("nature_connect");
        mv.addObject("authors", events);
        mv.addObject("bookings", bookings);

        return mv;
    }

    @GetMapping("/ui/events")
    public String showEventList() {
        return "redirect:/events";
    }
}
