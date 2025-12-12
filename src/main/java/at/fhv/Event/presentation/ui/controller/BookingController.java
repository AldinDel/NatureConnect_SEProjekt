package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.booking.BookingPermissionService;
import at.fhv.Event.application.equipment.GetRentableEquipmentService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.application.request.booking.ParticipantDTO;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.exception.BookingValidationException;
import at.fhv.Event.domain.model.exception.EventFullyBookedException;
import at.fhv.Event.domain.model.exception.ValidationError;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookEventService _bookEventService;
    private final GetEventDetailsService _eventDetailsService;
    private final GetRentableEquipmentService _rentableEquipmentService;
    private final BookingPermissionService _bookingPermissionService;

    public BookingController(BookEventService bookEventService,
                             GetEventDetailsService eventDetailsService,
                             GetRentableEquipmentService rentableEquipmentService,
                             BookingPermissionService bookingPermissionService) {
        _bookEventService = bookEventService;
        _eventDetailsService = eventDetailsService;
        _rentableEquipmentService = rentableEquipmentService;
        _bookingPermissionService = bookingPermissionService;
    }

    @GetMapping("/{eventId}")
    public String showBookingPage(@PathVariable Long eventId, Model model, RedirectAttributes redirectAttributes,Principal principal) {

        if (principal == null) {
            return "redirect:/booking/guest-info?eventId=" + eventId;
        }

        EventDetailDTO event = _eventDetailsService.getEventDetails(eventId);
        int availableSeats = _bookEventService.getAvailableSeats(eventId);
        if (isEventUnavailable(event)) {
            redirectAttributes.addFlashAttribute("error", getUnavailabilityMessage(event));
            return "redirect:/events/" + eventId;
        }

        CreateBookingRequest bookingRequest = createInitialBookingRequest(eventId);
        List<EquipmentDTO> availableEquipment = event.equipments();

        model.addAttribute("event", event);
        model.addAttribute("booking", bookingRequest);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("availableSeats", Math.max(0, availableSeats)); // Add this line
        model.addAttribute("isEdit", false);
        model.addAttribute("bookingId", null);

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

            String paymentMethod = booking.getPaymentMethod() != null
                    ? booking.getPaymentMethod().name()
                    : null;
            model.addAttribute("paymentMethod", paymentMethod);

            return "booking/payment";
        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error", "Booking not found");
            return "redirect:/events";
        }
    }

    @PostMapping("/payment/{id}")
    public String updatePaymentMethodFromUI(@PathVariable Long id, @RequestParam("paymentMethod") String paymentMethod, RedirectAttributes redirectAttributes) {
        try {
            _bookEventService.updatePaymentMethod(id, paymentMethod);
            return "redirect:/booking/payment/" + id;

        } catch (Exception exception) {
            redirectAttributes.addFlashAttribute("error",
                    "Payment method update failed: " + exception.getMessage());
            return "redirect:/booking/payment/" + id;
        }
    }

    @GetMapping("/confirmation/{id}")
    public String showConfirmationPage(@PathVariable Long id, Model model) {
        try {
            Booking booking = _bookEventService.getById(id);
            if (booking.getStatus() == BookingStatus.PAYMENT_FAILED) {
                return "redirect:/booking/payment/" + id;
            }

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
    public String guestInfoPage(@RequestParam Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
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
        List<EquipmentDTO> availableEquipment = event.equipments();

        model.addAttribute("fieldErrors", fieldErrors);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);
        model.addAttribute("isEdit", false);
        model.addAttribute("bookingId", null);

        return "booking/booking-page";
    }

    private String handleUnexpectedError(Exception exception, CreateBookingRequest request, Model model) {
        EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
        List<EquipmentDTO> availableEquipment = event.equipments();

        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);
        model.addAttribute("isEdit", false);
        model.addAttribute("bookingId", null);

        return "booking/booking-page";
    }

    private String handleEditValidationErrors(Long bookingId,
                                              BookingValidationException exception,
                                              CreateBookingRequest request,
                                              Model model) {

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
        List<EquipmentDTO> availableEquipment = event.equipments();

        model.addAttribute("fieldErrors", fieldErrors);
        model.addAttribute("errors", errorMessages);
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);

        model.addAttribute("isEdit", true);
        model.addAttribute("bookingId", bookingId);

        return "booking/booking-page";
    }

    private String handleEditUnexpectedError(Long bookingId,
                                             Exception exception,
                                             CreateBookingRequest request,
                                             Model model) {
        EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
        List<EquipmentDTO> availableEquipment = event.equipments();

        model.addAttribute("error", "An unexpected error occurred. Please try again.");
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);

        model.addAttribute("isEdit", true);
        model.addAttribute("bookingId", bookingId);

        return "booking/booking-page";
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'FRONT', 'ORGANIZER')")
    public String showEditBookingForm(@PathVariable("id") Long id,
                                      Model model,
                                      RedirectAttributes redirectAttributes,
                                      Authentication auth) {
        try {

            if (!_bookingPermissionService.canEdit(auth, id)) {
                redirectAttributes.addFlashAttribute("error", "You don't have the permission to edit this booking.");
                return "redirect:/bookings";
            }

            Booking booking = _bookEventService.getById(id);

            try {
                _bookEventService.assertEventIsEditableForBooking(booking);
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/bookings";
            }

            EventDetailDTO event = _eventDetailsService.getEventDetails(booking.getEventId());

            List<EquipmentDTO> availableEquipment = event.equipments();
            CreateBookingRequest request = mapBookingToCreateBookingRequest(booking);

            model.addAttribute("event", event);
            model.addAttribute("booking", request);
            model.addAttribute("addons", availableEquipment);

            Set<Long> selectedEquipmentIds = booking.getEquipment() != null
                    ? booking.getEquipment().stream()
                    .map(BookingEquipment::getEquipmentId)
                    .collect(Collectors.toSet())
                    : Set.of();
            model.addAttribute("selectedEquipmentIds", selectedEquipmentIds);

            model.addAttribute("isEdit", true);
            model.addAttribute("bookingId", id);

            return "booking/booking-page";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Booking not found.");
            return "redirect:/bookings";
        }
    }


    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'FRONT', 'ORGANIZER')")
    public String updateBooking(@PathVariable("id") Long id,
                                @ModelAttribute("booking") CreateBookingRequest request,
                                @RequestParam(name = "changePayment", required = false, defaultValue = "false") boolean changePayment,
                                Model model,
                                RedirectAttributes redirectAttributes,
                                Authentication auth) {
        try {
            if (!_bookingPermissionService.canEdit(auth, id)) {
                redirectAttributes.addFlashAttribute("error", "You don't have the permission to edit this booking.");
                return "redirect:/bookings";
            }

            Booking existingBooking = _bookEventService.getById(id);
            request.setEventId(existingBooking.getEventId());

            _bookEventService.updateBooking(id, request);

            boolean isAdmin = auth != null &&
                    auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (changePayment && isAdmin) {
                redirectAttributes.addFlashAttribute("success", "Booking updated. You can now change the payment method.");
                return "redirect:/booking/payment/" + id;
            }

            redirectAttributes.addFlashAttribute("success", "Booking updated successfully.");
            return "redirect:/bookings";

        } catch (BookingValidationException exception) {
            return handleEditValidationErrors(id, exception, request, model);

        } catch (EventFullyBookedException exception) {
            redirectAttributes.addFlashAttribute("error", exception.getMessage());
            return "redirect:/booking/" + id + "/edit";

        } catch (Exception exception) {
            return handleEditUnexpectedError(id, exception, request, model);
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    public String cancelBooking(@PathVariable Long id,
                                Authentication auth,
                                Principal principal,
                                RedirectAttributes redirectAttributes) {

        try {
            boolean isAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            _bookEventService.cancelBooking(id, principal.getName(), isAdmin);

            redirectAttributes.addFlashAttribute("success", "Booking cancelled successfully.");

            redirectAttributes.addFlashAttribute(
                    "info",
                    "Refund email has been sent to the customer."
            );

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
            return "redirect:/bookings";
        }

        return "redirect:/bookings";
    }





    private CreateBookingRequest mapBookingToCreateBookingRequest(Booking booking) {
        CreateBookingRequest req = new CreateBookingRequest();

        req.setEventId(booking.getEventId());
        req.setBookerFirstName(booking.getBookerFirstName());
        req.setBookerLastName(booking.getBookerLastName());
        req.setBookerEmail(booking.getBookerEmail());
        req.setSeats(booking.getSeats());
        req.setAudience(booking.getAudience());
        req.setVoucherCode(booking.getVoucherCode());
        req.setSpecialNotes(booking.getSpecialNotes());

        if (booking.getParticipants() != null) {
            List<ParticipantDTO> participants = booking.getParticipants().stream().map(p -> {
                ParticipantDTO dto = new ParticipantDTO();
                dto.setFirstName(p.getFirstName());
                dto.setLastName(p.getLastName());
                dto.setAge(p.getAge());
                return dto;
            }).toList();
            req.setParticipants(participants);
        }

        if (booking.getEquipment() != null) {
            Map<Long, EquipmentSelection> equipmentMap = new HashMap<>();
            for (BookingEquipment be : booking.getEquipment()) {
                EquipmentSelection sel = new EquipmentSelection();
                sel.setSelected(true);
                sel.setQuantity(be.getQuantity());
                equipmentMap.put(be.getEquipmentId(), sel);
            }
            req.setEquipment(equipmentMap);
        }


        return req;
    }


}