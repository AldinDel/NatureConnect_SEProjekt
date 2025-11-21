package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.domain.model.booking.Booking;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking")
public class ConfirmationController {

    private final BookEventService bookEventService;

    public ConfirmationController(BookEventService bookEventService) {
        this.bookEventService = bookEventService;
    }

    @GetMapping("/confirmation/{id}")
    public String confirmation(@PathVariable Long id, Model model) {

        Booking booking = bookEventService.getById(id);

        model.addAttribute("booking", booking);
        model.addAttribute("bookingId", booking.getId());
        model.addAttribute("amount", booking.getTotalPrice());
        model.addAttribute("paymentMethod", booking.getPaymentMethod());

        return "booking/confirmation";
    }
}

