package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.GetAllBookingsService;
import at.fhv.Event.application.booking.GetUserBookingsService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import at.fhv.Event.presentation.rest.response.booking.BookingWithEventDTO;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
    @Transactional(readOnly = true)
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

            Set<Long> eventIds = bookings.stream()
                    .map(BookingDTO::getEventId)
                    .collect(Collectors.toSet());

            Map<Long, EventDetailDTO> eventsById = eventDetailsService.getEventsByIds(eventIds)
                    .stream()
                    .collect(Collectors.toMap(EventDetailDTO::id, e -> e));

            bookings.forEach(b -> {
                var event = eventsById.get(b.getEventId());
                if (event != null) {
                    LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());
                    boolean editable = !Boolean.TRUE.equals(event.cancelled())
                            && !eventStart.isBefore(LocalDateTime.now());
                    b.setEditable(editable);
                }
            });

            model.addAttribute("bookings", bookings);
            return "booking/bookings-admin-overview";
        }

        List<Booking> userBookings = userBookingsService.getBookingsByUserEmail(email);

        Set<Long> userEventIds = userBookings.stream()
                .map(Booking::getEventId)
                .collect(Collectors.toSet());

        Map<Long, EventDetailDTO> userEventsById = eventDetailsService.getEventsByIds(userEventIds)
                .stream()
                .collect(Collectors.toMap(EventDetailDTO::id, e -> e));

        List<BookingWithEventDTO> bookingDTOs = userBookings.stream()
                .map(b -> new BookingWithEventDTO(
                        b,
                        userEventsById.get(b.getEventId())
                ))
                .toList();

        model.addAttribute("bookings", bookingDTOs);

        return "booking/bookings-overview";
    }

    @GetMapping("/bookings/all")
    public String allBookingsPage(Model model, Principal principal) {

        if (principal == null) {
            return "booking/booking-info";
        }

        boolean allowed =
                principal.toString().contains("ADMIN") ||
                        principal.toString().contains("FRONT") ||
                        principal.toString().contains("ORGANIZER");

        if (!allowed) {
            return "redirect:/bookings";
        }

        var bookings = getAllBookingsService.getAllBookings();

        Set<Long> eventIds = bookings.stream()
                .map(BookingDTO::getEventId)
                .collect(Collectors.toSet());

        Map<Long, EventDetailDTO> eventsById = eventDetailsService.getEventsByIds(eventIds)
                .stream()
                .collect(Collectors.toMap(EventDetailDTO::id, e -> e));

        bookings.forEach(b -> {
            var event = eventsById.get(b.getEventId());
            if (event != null) {
                LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());
                boolean editable = !Boolean.TRUE.equals(event.cancelled())
                        && !eventStart.isBefore(LocalDateTime.now());
                b.setEditable(editable);
            }
        });

        model.addAttribute("bookings", bookings);

        return "booking/bookings-admin-overview";
    }


}
