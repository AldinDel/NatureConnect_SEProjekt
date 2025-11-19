package at.fhv.Event.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/booking")
public class ConfirmationController {

    @GetMapping("/confirmation/{id}")
    public String confirmation(
            @PathVariable Long id,
            @RequestParam("tx") Long transactionId,
            Model model
    ) {
        model.addAttribute("bookingId", id);
        model.addAttribute("transactionId", transactionId);
        return "booking/confirmation";
    }
}
