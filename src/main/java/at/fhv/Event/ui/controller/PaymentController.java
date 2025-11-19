package at.fhv.Event.ui.controller;

import at.fhv.Event.application.booking.ProcessPaymentService;
import at.fhv.Event.domain.model.booking.PaymentMethod;
import at.fhv.Event.domain.model.booking.PaymentTransaction;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
@Controller
@RequestMapping("/booking/payment")
public class PaymentController {

    private final ProcessPaymentService processPaymentService;

    public PaymentController(ProcessPaymentService processPaymentService) {
        this.processPaymentService = processPaymentService;
    }

    @GetMapping("/{bookingId}")
    public String showPaymentPage(@PathVariable Long bookingId, Model model) {
        model.addAttribute("bookingId", bookingId);
        return "booking/payment";
    }

    @PostMapping("/{bookingId}")
    public String processPayment(
            @PathVariable Long bookingId,
            @RequestParam("paymentMethod") PaymentMethod method
    ) {
        PaymentTransaction tx = processPaymentService.processPayment(bookingId, method);

        return "redirect:/booking/confirmation/" + bookingId + "?tx=" + tx.getId();
    }
}
