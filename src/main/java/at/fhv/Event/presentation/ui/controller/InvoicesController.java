package at.fhv.Event.presentation.ui.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class InvoicesController {

    @GetMapping("/event_management/invoices")
    public String showInvoices(@RequestParam("eventId") Long eventId, Model model) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "invoices");
        return "event_management/invoices";
    }
}
