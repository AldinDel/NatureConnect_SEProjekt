package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/booking")
public class BookingController {

    private final BookEventService bookEventService;
    private final GetEventDetailsService getEventDetailsService;
    private final EquipmentJpaRepository equipmentRepository;

    public BookingController(
            BookEventService bookEventService,
            GetEventDetailsService getEventDetailsService,
            EquipmentJpaRepository equipmentRepository
    ) {
        this.bookEventService = bookEventService;
        this.getEventDetailsService = getEventDetailsService;
        this.equipmentRepository = equipmentRepository;
    }

    @GetMapping("/{eventId}")
    public String showBookingPage(@PathVariable Long eventId,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        EventDetailDTO event = getEventDetailsService.getEventDetails(eventId);

        // cancelled Events cant open booking page over url
        if (Boolean.TRUE.equals(event.cancelled())) {
            redirectAttributes.addFlashAttribute("error", "This event is cancelled and cannot be booked.");
            return "redirect:/events/" + eventId;
        }

        // same for expired events
        LocalDateTime start = LocalDateTime.of(event.date(), event.startTime());
        if (start.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "This event is expired and cannot be booked.");
            return "redirect:/events/" + eventId;
        }

        CreateBookingRequest req = new CreateBookingRequest();
        req.setEventId(eventId);
        req.setAudience(AudienceType.INDIVIDUAL);

        List<EquipmentEntity> addons = equipmentRepository.findByRentableTrue();

        model.addAttribute("addons", addons);
        model.addAttribute("event", event);
        model.addAttribute("booking", req);

        return "booking/booking-page";
    }

    @PostMapping
    public String submitBooking(@ModelAttribute("booking") CreateBookingRequest request,
                                RedirectAttributes redirectAttributes) {

        EventDetailDTO event = getEventDetailsService.getEventDetails(request.getEventId());

        if (Boolean.TRUE.equals(event.cancelled())) {
            redirectAttributes.addFlashAttribute("error", "This event is cancelled and cannot be booked.");
            return "redirect:/events/" + event.id();
        }

        LocalDateTime start = LocalDateTime.of(event.date(), event.startTime());
        if (start.isBefore(LocalDateTime.now())) {
            redirectAttributes.addFlashAttribute("error", "This event is expired and cannot be booked.");
            return "redirect:/events/" + event.id();
        }

        BookingDTO bookingDTO = bookEventService.bookEvent(request);
        Long bookingId = bookingDTO.getId();
        return "redirect:/booking/payment/" + bookingId;
    }


    @GetMapping("/payment/{id}")
    public String paymentPage(@PathVariable Long id, Model model) {
        Booking booking = bookEventService.getById(id);

        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("amount", booking.getTotalPrice());
        model.addAttribute("paymentMethod", booking.getPaymentMethod());

        return "booking/payment";
    }

    @PostMapping("/payment/{id}")
    public String completePayment(
            @PathVariable Long id,
            @RequestParam("paymentMethod") String paymentMethod
    ) {
        bookEventService.updatePaymentMethod(id, paymentMethod);

        return "redirect:/booking/confirmation/" + id;
    }
}
