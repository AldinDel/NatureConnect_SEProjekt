package at.fhv.Event.presentation.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CheckOutController {

    @GetMapping("/event_management/checkout")
    public String showCheckout(@RequestParam("eventId") Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "checkout");
        return "event_management/checkout";
    }
}
