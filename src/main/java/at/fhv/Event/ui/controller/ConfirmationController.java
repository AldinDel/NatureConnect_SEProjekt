package at.fhv.Event.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/booking")
public class ConfirmationController {
    @GetMapping("/confirmation/{id}")
    public String confirmation(
            @PathVariable Long id,
            Model model
    ) {
        model.addAttribute("bookingId", id);
        return "booking/confirmation";
    }
}
