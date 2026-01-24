package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.booking.BookingCalendarService;
import at.fhv.Event.application.booking.BookingPermissionService;
import at.fhv.Event.application.booking.BookingPrefillService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.invoice.CreateFinalInvoiceService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.exception.*;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(BookingController.class);

    private final BookEventService _bookEventService;
    private final GetEventDetailsService _eventDetailsService;
    private final BookingPermissionService _bookingPermissionService;
    private final BookingPrefillService _bookingPrefillService;
    private final CreateFinalInvoiceService _createFinalInvoiceService;
    private final BookingCalendarService _bookingCalendarService;
    private final ErrorMessageService _errorMessageService;

    public BookingController(BookEventService bookEventService,
                             GetEventDetailsService eventDetailsService,
                             BookingPermissionService bookingPermissionService,
                             BookingPrefillService bookingPrefillService,
                             CreateFinalInvoiceService createFinalInvoiceService,
                             BookingCalendarService bookingCalendarService,
                             ErrorMessageService errorMessageService) {
        _bookEventService = bookEventService;
        _eventDetailsService = eventDetailsService;
        _bookingPermissionService = bookingPermissionService;
        _bookingPrefillService = bookingPrefillService;
        _createFinalInvoiceService = createFinalInvoiceService;
        _bookingCalendarService = bookingCalendarService;
        _errorMessageService = errorMessageService;
    }

    @GetMapping("/event/{eventId}")
    @PreAuthorize("isAuthenticated()")
    public String showBookingPage(@PathVariable Long eventId,
                                  Model model,
                                  RedirectAttributes redirectAttributes,
                                  Principal principal) {

        try {
            EventDetailDTO event = _eventDetailsService.getEventDetails(eventId);
            int availableSeats = _bookEventService.getAvailableSeats(eventId);

            if (isEventUnavailable(event)) {
                redirectAttributes.addFlashAttribute("error", getUnavailabilityMessage(event));
                return "redirect:/events/" + eventId;
            }

            if (!model.containsAttribute("booking")) {
                CreateBookingRequest bookingRequest =
                        _bookingPrefillService.prepareCreateRequestForLoggedInUser(
                                principal.getName(),
                                eventId
                        );
                model.addAttribute("booking", bookingRequest);
            }

            List<EquipmentDTO> availableEquipment = event.equipments();

            model.addAttribute("event", event);
            model.addAttribute("addons", availableEquipment);
            model.addAttribute("availableSeats", Math.max(0, availableSeats));
            model.addAttribute("isEdit", false);
            model.addAttribute("bookingId", null);
            model.addAttribute("allowedDays", _bookingCalendarService.resolveAllowedDays(event));

            return "booking/booking-page";
        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (Exception e) {
            logger.error("Failed to load booking page for event {}", eventId, e);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";
        }
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public String submitBooking(@ModelAttribute("booking") CreateBookingRequest request, Model model, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            EventDetailDTO event = _eventDetailsService.getEventDetails(request.getEventId());
            if (isEventUnavailable(event)) {
                redirectAttributes.addFlashAttribute("error", getUnavailabilityMessage(event));
                return "redirect:/events/" + event.id();
            }

            BookingDTO booking = _bookEventService.bookEvent(request);
            return "redirect:/booking/payment/" + booking.getId();


        } catch (EventFullyBookedException exception) {
            logger.warn("Event fully booked: {}", exception.getMessage());
            String message = exception.getAvailableSeats() == 0
                    ? _errorMessageService.getMessage("BOOKING_003_FULLY")
                    : _errorMessageService.getMessage(
                    exception.getErrorCode(),
                    exception.getAvailableSeats(),
                    exception.getRequestedSeats()
            );
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events/" + request.getEventId();

        } catch (BookingValidationException exception) {
            Map<String, String> fieldErrors = new HashMap<>();
            List<String> errorMessages = new ArrayList<>();

            for (ValidationError error : exception.getErrors()) {
                String field = error.get_field();
                String message = error.get_message();

                if (fieldErrors.containsKey(field)) {
                    fieldErrors.put(field, fieldErrors.get(field) + "; " + message);
                } else {
                    fieldErrors.put(field, message);
                }
                errorMessages.add(message);
            }

            redirectAttributes.addFlashAttribute("fieldErrors", fieldErrors);
            redirectAttributes.addFlashAttribute("errors", errorMessages);
            redirectAttributes.addFlashAttribute("booking", request);

            return "redirect:/booking/event/" + request.getEventId();

        } catch (InsufficientStockException e) {
            logger.warn("Insufficient stock: {}", e.getMessage());
            String message = _errorMessageService.getMessage(
                    e.getErrorCode(),
                    e.getEquipmentName(),
                    e.getAvailableQuantity(),
                    e.getRequestedQuantity()
            );
            redirectAttributes.addFlashAttribute("error", message);
            redirectAttributes.addFlashAttribute("booking", request);
            return "redirect:/booking/event/" + request.getEventId();

        } catch (Exception exception) {
            logger.error("Unexpected error during booking submission", exception);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            redirectAttributes.addFlashAttribute("booking", request);
            return "redirect:/booking/event/" + request.getEventId();
        }
    }

    @GetMapping("/payment/{id}")
    @PreAuthorize("isAuthenticated()")
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
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (Exception exception) {
            logger.error("Failed to load payment page for booking {}", id, exception);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";
        }
    }

    @PostMapping("/payment/{id}")
    @PreAuthorize("isAuthenticated()")
    public String updatePaymentMethodFromUI(@PathVariable Long id, @RequestParam("paymentMethod") String paymentMethod, RedirectAttributes redirectAttributes) {
        try {
            logger.info("Updating payment method for booking {} to {}", id, paymentMethod);
            _bookEventService.updatePaymentMethod(id, paymentMethod);

            // For INVOICE payment, create final invoice immediately
            if ("INVOICE".equals(paymentMethod)) {
                try {
                    logger.info("Creating final invoice for INVOICE payment method, booking {}", id);
                    _createFinalInvoiceService.createFinalInvoiceForBooking(id);
                    logger.info("Final invoice created for booking {}", id);
                } catch (Exception e) {
                    logger.error("Failed to create final invoice for booking {}: {}", id, e.getMessage(), e);
                    // Continue anyway, booking is still confirmed
                }
            }

            return "redirect:/booking/payment/" + id;

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found for payment update: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (PaymentOperationException e) {
            logger.error("Payment operation failed: {}", e.getMessage(), e);
            String message = _errorMessageService.getMessage(
                    e.getErrorCode(),
                    e.getBookingId(),
                    e.getReason()
            );
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/booking/payment/" + id;

        } catch (IllegalArgumentException e) {
            logger.error("Invalid payment method: {}", paymentMethod, e);
            redirectAttributes.addFlashAttribute("error", "Invalid payment method selected.");
            return "redirect:/booking/payment/" + id;

        } catch (Exception exception) {
            logger.error("Unexpected error updating payment method for booking {}", id, exception);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/booking/payment/" + id;
        }
    }

    @GetMapping("/confirmation/{id}")
    @PreAuthorize("isAuthenticated()")
    public String showConfirmationPage(@PathVariable Long id, @RequestParam(required = false) String paymentMethod, Model model, RedirectAttributes redirectAttributes) {
        try {
            Booking booking = _bookEventService.getById(id);
            if (booking.getStatus() == BookingStatus.PAYMENT_FAILED) {
                return "redirect:/booking/payment/" + id;
            }

            model.addAttribute("booking", booking);
            model.addAttribute("bookingId", booking.getId());
            model.addAttribute("amount", booking.getTotalPrice());

            String finalPaymentMethod = paymentMethod != null
                    ? paymentMethod
                    : (booking.getPaymentMethod() != null ? booking.getPaymentMethod().name() : null);
            model.addAttribute("paymentMethod", finalPaymentMethod != null
                    ? at.fhv.Event.domain.model.payment.PaymentMethod.valueOf(finalPaymentMethod)
                    : null);

            return "booking/confirmation";

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found for confirmation: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (Exception exception) {
            logger.error("Failed to load confirmation page for booking {}", id, exception);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";
        }
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

            Booking booking = _bookEventService.getByIdWithParticipants(id);

            try {
                _bookEventService.assertEventIsEditableForBooking(booking);
            } catch (IllegalStateException ex) {
                redirectAttributes.addFlashAttribute("error", ex.getMessage());
                return "redirect:/bookings";
            }

            EventDetailDTO event = _eventDetailsService.getEventDetails(booking.getEventId());

            List<EquipmentDTO> availableEquipment = event.equipments();
            CreateBookingRequest request = _bookingPrefillService.prepareEditRequest(booking, availableEquipment);
            request.setHikeRouteKey(booking.getHikeRouteKey());

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

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/bookings";

        } catch (EventNotFoundException e) {
            logger.error("Event not found for booking: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/bookings";

        } catch (Exception e) {
            logger.error("Failed to load booking edit form: {}", id, e);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
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
            logger.warn("Event fully booked during update: {}", exception.getMessage());
            String message = _errorMessageService.getMessage(
                    exception.getErrorCode(),
                    exception.getAvailableSeats(),
                    exception.getRequestedSeats()
            );
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/booking/" + id + "/edit";

        } catch (InsufficientStockException e) {
            logger.warn("Insufficient stock during booking update: {}", e.getMessage());
            String message = _errorMessageService.getMessage(
                    e.getErrorCode(),
                    e.getEquipmentName(),
                    e.getAvailableQuantity(),
                    e.getRequestedQuantity()
            );
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/booking/" + id + "/edit";

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found for update: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/bookings";

        } catch (Exception exception) {
            logger.error("Unexpected error updating booking: {}", id, exception);
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

        } catch (BookingNotFoundException e) {
            logger.error("Booking not found for cancellation: {}", id, e);
            String message = _errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);

        } catch (BookingOperationException e) {
            logger.error("Booking cancellation failed: {}", e.getMessage(), e);
            String message = _errorMessageService.getMessage(
                    e.getErrorCode(),
                    e.getOperation(),
                    e.getReason()
            );
            redirectAttributes.addFlashAttribute("error", message);

        } catch (Exception e) {
            logger.error("Unexpected error cancelling booking: {}", id, e);
            String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
        }

        return "redirect:/bookings";
    }

    private boolean isEventUnavailable(EventDetailDTO event) {
        if (Boolean.TRUE.equals(event.cancelled())) {
            return true;
        }

        if (event.recurring()) {
            return false;
        }

        if (event.date() == null || event.startTime() == null) {
            return false;
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

        String message = _errorMessageService.getMessage("UNEXPECTED_ERROR");
        model.addAttribute("error", message);
        model.addAttribute("event", event);
        model.addAttribute("addons", availableEquipment);
        model.addAttribute("booking", request);

        model.addAttribute("isEdit", true);
        model.addAttribute("bookingId", bookingId);

        return "booking/booking-page";
    }

}