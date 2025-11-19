package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
    public String showBookingPage(@PathVariable Long eventId, Model model) {

        EventDetailDTO event = getEventDetailsService.getEventDetails(eventId);

        CreateBookingRequest req = new CreateBookingRequest();
        req.setEventId(eventId);

        List<EquipmentEntity> addons = equipmentRepository.findByRentableTrue();

        model.addAttribute("addons", addons);
        model.addAttribute("event", event);
        model.addAttribute("booking", req);

        return "booking/booking-page";
    }

    @PostMapping
    public String submitBooking(
            @ModelAttribute("booking") CreateBookingRequest request
    ) {
        BookingDTO bookingDTO = bookEventService.bookEvent(request);

        Long bookingId = bookingDTO.getId();

        // Redirect to payment page
        return "redirect:/booking/payment/" + bookingId;
    }
}
