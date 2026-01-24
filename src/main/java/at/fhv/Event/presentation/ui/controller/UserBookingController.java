package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.BookingStatusService;
import at.fhv.Event.application.booking.GetAllBookingsService;
import at.fhv.Event.application.booking.GetUserBookingsService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import at.fhv.Event.presentation.rest.response.booking.BookingWithEventDTO;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class UserBookingController {

    private static final Logger logger = LoggerFactory.getLogger(UserBookingController.class);

    private final GetUserBookingsService userBookingsService;
    private final GetEventDetailsService eventDetailsService;
    private final GetAllBookingsService getAllBookingsService;
    private final BookingStatusService bookingStatusService;
    private final ErrorMessageService errorMessageService;


    public UserBookingController(GetUserBookingsService userBookingsService,GetEventDetailsService eventDetailsService,GetAllBookingsService getAllBookingsService, BookingStatusService bookingStatusService, ErrorMessageService errorMessageService) {
        this.userBookingsService = userBookingsService;
        this.eventDetailsService = eventDetailsService;
        this.getAllBookingsService = getAllBookingsService;
        this.bookingStatusService = bookingStatusService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/bookings")
    @Transactional(readOnly = true)
    public String bookingsPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "booking/booking-info";
        }

        try {
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
                    if (event != null && event.date() != null && event.startTime() != null) {

                        LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());

                        boolean editable = !Boolean.TRUE.equals(event.cancelled())
                                && !eventStart.isBefore(LocalDateTime.now());

                        b.setEditable(editable);
                    } else {
                        b.setEditable(false);
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
                    .map(b -> {
                        EventDetailDTO event = userEventsById.get(b.getEventId());

                        boolean expired = bookingStatusService.isExpired(event);
                        boolean inactive = bookingStatusService.isInactive(b, event);
                        boolean cancelled = bookingStatusService.isCancelled(b, event);

                        return new BookingWithEventDTO(
                                b,
                                event,
                                expired,
                                inactive,
                                cancelled
                        );
                    })
                    .toList();

            model.addAttribute("bookings", bookingDTOs);

            return "booking/bookings-overview";
        } catch (Exception e) {
            logger.error("Failed to load bookings for user {}", principal.getName(), e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            model.addAttribute("bookings", List.of());
            return "booking/bookings-overview";
        }
    }

    @GetMapping("/bookings/all")
    public String allBookingsPage(Model model, Principal principal, RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "booking/booking-info";
        }

        try {
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
                } else {
                    b.setEditable(false);
                }
            });

            model.addAttribute("bookings", bookings);

            return "booking/bookings-admin-overview";
        } catch (Exception e) {
            logger.error("Failed to load all bookings", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            model.addAttribute("bookings", List.of());
            return "booking/bookings-admin-overview";
        }
    }


}
