package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.equipment.GetRentableEquipmentService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.exception.BookingValidationException;
import at.fhv.Event.domain.model.exception.EventFullyBookedException;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.rest.response.booking.BookingDTO;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookEventService _bookEventService;
    private final GetEventDetailsService _eventDetailsService;
    private final GetRentableEquipmentService _rentableEquipmentService;

    public BookingController(BookEventService bookEventService, GetEventDetailsService eventDetailsService, GetRentableEquipmentService rentableEquipmentService) {
        _bookEventService = bookEventService;
        _eventDetailsService = eventDetailsService;
        _rentableEquipmentService = rentableEquipmentService;
    }

    @GetMapping("/{eventId}")
    public String showBookingPage(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes) {
        EventDetailDTO event = _eventDetailsService.getEventDetails(eventId);
        if (isEventUnavailable(event)) {
            redirectAttributes.addFlashAttribute("error", getUnavailabilityMessage(event));
            return "redirect:/events/" + eventId;
        }

        CreateBookingRequest bookingRequest = createInitialBookingRequest(eventId);
        List<EquipmentDTO> availableEquipment = _rentableEquipmentService.getRentableEquipment();
        model.addAttribute("event", event);
        model.addAttribute("booking", bookingRequest);
        model.addAttribute("addons", availableEquipment);
        return "booking/booking-page";
    }

    @PostMapping
    public String submitBooking(@ModelAttribute("booking") CreateBookingRequest request, @RequestParam("guestMode") boolean guestMode, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
            if (isEventUnavailable(event)) {
                redirectAttributes.addFlashAttribute("error", getUnavailabilityMessage(event));
                return "redirect:/events/" + event.id();
            }

            BookingDTO booking = _bookEventService.bookEvent(request);

            if (principal != null) {
                return "redirect:/booking/payment/" + booking.getId();
            }
            return "redirect:/booking/guest-info?bookingId=" + booking.getId();

        } catch (BookingValidationException exception) {
            return handleValidationErrors(exception, request, model);

        } catch (EventFullyBookedException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/events/" + request.getEventId();

        } catch (Exception exception) {
            return handleUnexpectedError(exception, request, model);
        }
    }

    @GetMapping("/payment/{id}")
    public String showPaymentPage(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = _bookEventService.getById(id);
            model.addAttribute("bookingId", booking.getId());
            model.addAttribute("amount", booking.getTotalPrice());
            model.addAttribute("paymentMethod", booking.getPaymentMethod());

            return "booking/payment";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/events";
        }
    }

    @PostMapping("/payment/{id}")
    public String processPayment(@PathVariable Long id, @RequestParam("paymentMethod") String paymentMethod, RedirectAttributes redirectAttributes) {
        try {
            _bookEventService.updatePaymentMethod(id, paymentMethod);
            return "redirect:/booking/confirmation/" + id;
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error",
                    "Payment processing failed: " + exception.getMessage());
            return "redirect:/booking/payment/" + id;
        }
    }

    @GetMapping("/confirmation/{id}")
    public String showConfirmationPage(@PathVariable Long id, Model model) {
        try {
            Booking booking = _bookEventService.getById(id);
            model.addAttribute("booking", booking);
            model.addAttribute("bookingId", booking.getId());
            model.addAttribute("amount", booking.getTotalPrice());
            model.addAttribute("paymentMethod", booking.getPaymentMethod());
            return "booking/confirmation";
        } catch (Exception exception) {
            model.addAttribute("error", "Booking not found");
            return "error/404";
        }
    }

    @GetMapping("/guest-info")
    public String guestInfoPage() {
        return "booking/guest-info";
    }


    private boolean isEventUnavailable(EventDetailDTO event) {
        if (Boolean.TRUE.equals(event.cancelled())) {
            return true;
        }
        LocalDateTime eventStart = LocalDateTime.of(event.date(), event.startTime());
        return eventStart.isBefore(LocalDateTime.now());
    }

    private String getUnavailabilityMessage(EventDetailDTO event) {
        if (Boolean.TRUE.equals(event.cancelled())) {
            return "This event is cancelled and cannot be booked.";
        }
        return "This event is expired and cannot be booked.";
    }

    private CreateBookingRequest createInitialBookingRequest(Long eventId) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setAudience(AudienceType.INDIVIDUAL);
        return request;
    }

    private String handleValidationErrors(BookingValidationException exception, CreateBookingRequest request, Model model) {

        Map<String, String> fieldErrors = new HashMap<>();
        List<String> errorMessages = new ArrayList<>();
        for (ValidationError error : exception.getErrors()) {
            String field = error.get_field();
            String message = error.get_message();

            if (fieldErrors.containsKey(field)) {
                String existing = fieldErrors.get(field);
                fieldErrors.put(field, existing + "; " + message);
            } else {
                fieldErrors.put(field, message);
            }
            errorMessages.add(message);
        }

        EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
        List<EquipmentDTO> availableEquipment = _rentableEquipmentService.getRentableEquipment();

        model.addAttribute("fieldErrors", fieldErrors);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);

        return "booking/booking-page";
    }

    private String handleUnexpectedError(Exception exception, CreateBookingRequest request, Model model) {
        EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
        List<EquipmentDTO> availableEquipment = _rentableEquipmentService.getRentableEquipment();

        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);

        return "booking/booking-page";
    }
}