package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.invoice.CreateInterimInvoiceService;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class InvoicesController {

    private final CreateInterimInvoiceService createInterimInvoiceService;
    private final InvoiceRepository invoiceRepository;

    public InvoicesController(
            CreateInterimInvoiceService createInterimInvoiceService,
            InvoiceRepository invoiceRepository
    ) {
        this.createInterimInvoiceService = createInterimInvoiceService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping("/event_management/invoices")
    public String showInvoices(
            @RequestParam("eventId") Long eventId,
            Model model
    ) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "invoices");
        model.addAttribute("invoices", invoiceRepository.findAll());

        return "event_management/invoices";
    }

    @PostMapping("/event_management/invoices/interim")
    public String createInterimInvoice(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam("eventId") Long eventId
    ) {
        createInterimInvoiceService.createInterimInvoice(
                bookingId,
                List.of() // Services kommen später
        );

        return "redirect:/event_management/invoices?eventId=" + eventId;
    }
}
