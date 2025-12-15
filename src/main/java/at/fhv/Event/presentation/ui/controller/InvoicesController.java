package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.invoice.CreateInterimInvoiceService;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import at.fhv.Event.application.equipment.GetRentableEquipmentService;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;


import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

@Controller
public class InvoicesController {

    private final CreateInterimInvoiceService createInterimInvoiceService;
    private final InvoiceRepository invoiceRepository;
    private final GetRentableEquipmentService getRentableEquipmentService;

    public InvoicesController(
            CreateInterimInvoiceService createInterimInvoiceService,
            InvoiceRepository invoiceRepository,
            GetRentableEquipmentService getRentableEquipmentService
    ) {
        this.createInterimInvoiceService = createInterimInvoiceService;
        this.invoiceRepository = invoiceRepository;
        this.getRentableEquipmentService = getRentableEquipmentService;
    }

    @GetMapping("/event_management/invoices")
    public String showInvoices(
            @RequestParam("eventId") Long eventId,
            @RequestParam(value = "created", required = false) Boolean created,
            Model model
    ) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "invoices");
        model.addAttribute(
                "invoices",
                invoiceRepository.findByEventId(eventId)
        );
        model.addAttribute("services", getRentableEquipmentService.getRentableEquipment());

        if (Boolean.TRUE.equals(created)) {
            model.addAttribute("successMessage", "Interim invoice created successfully.");
        }

        return "event_management/invoices";
    }


    @PostMapping("/event_management/invoices/interim")
    public String createInterimInvoice(
            @RequestParam("eventId") Long eventId,
            @RequestParam(value = "equipmentIds", required = false) List<Long> equipmentIds
    ) {
        createInterimInvoiceService.createInterimInvoice(
                eventId,
                equipmentIds
        );

        return "redirect:/event_management/invoices?eventId="
                + eventId + "&created=true";
    }

}
