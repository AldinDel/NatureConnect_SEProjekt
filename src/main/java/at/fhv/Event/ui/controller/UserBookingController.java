package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.GetAllBookingsService;
import at.fhv.Event.application.booking.GetUserBookingsService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.rest.response.booking.BookingWithEventDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Controller
public class UserBookingController {

    private final GetUserBookingsService userBookingsService;
    private final GetEventDetailsService eventDetailsService;
    private final GetAllBookingsService getAllBookingsService;


    public UserBookingController(GetUserBookingsService userBookingsService,GetEventDetailsService eventDetailsService,GetAllBookingsService getAllBookingsService) {
        this.userBookingsService = userBookingsService;
        this.eventDetailsService = eventDetailsService;
        this.getAllBookingsService = getAllBookingsService;
    }

    @GetMapping("/bookings")
    public String bookingsPage(Model model, Principal principal) {

        if (principal == null) {
            return "booking/booking-info";
        }

        String email = principal.getName();

        boolean isStaff =
                principal.toString().contains("ADMIN") ||
                        principal.toString().contains("FRONT") ||
                        principal.toString().contains("ORGANIZER");

        if (isStaff) {
            var bookings = getAllBookingsService.getAllBookings();

            bookings.forEach(b -> {
                var event = eventDetailsService.getEventDetails(b.getEventId());
                LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());
                boolean editable = !Boolean.TRUE.equals(event.cancelled())
                        && !eventStart.isBefore(LocalDateTime.now());
                b.setEditable(editable);
            });

            model.addAttribute("bookings", bookings);
            return "booking/bookings-admin-overview";
        }

        List<Booking> userBookings = userBookingsService.getBookingsByUserEmail(email);

        List<BookingWithEventDTO> bookingDTOs = userBookings.stream()
                .map(b -> new BookingWithEventDTO(
                        b,
                        eventDetailsService.getEventDetails(b.getEventId())
                ))
                .toList();

        model.addAttribute("bookings", bookingDTOs);

        return "booking/bookings-overview";
    }

    @GetMapping("/bookings/all")
    public String allBookingsPage(Model model, Principal principal) {

        if (principal == null) return "booking/booking-info";

        boolean allowed =
                principal.toString().contains("ADMIN") ||
                        principal.toString().contains("FRONT") ||
                        principal.toString().contains("ORGANIZER");

        if (!allowed) {
            return "redirect:/bookings";
        }

        var bookings = getAllBookingsService.getAllBookings();

        bookings.forEach(b -> {
            var event = eventDetailsService.getEventDetails(b.getEventId());
            LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());
            boolean editable = !Boolean.TRUE.equals(event.cancelled())
                    && !eventStart.isBefore(LocalDateTime.now());
            b.setEditable(editable);
        });

        model.addAttribute("bookings", bookings);

        return "booking/bookings-admin-overview";
    }


}
