package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.equipment.GetRentableEquipmentService;
import at.fhv.Event.application.invoice.CreateInterimInvoiceService;
import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
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
    private final GetRentableEquipmentService getRentableEquipmentService;
    private final GetParticipantsForEventService participantsService;

    public InvoicesController(
            CreateInterimInvoiceService createInterimInvoiceService,
            InvoiceRepository invoiceRepository,
            GetRentableEquipmentService getRentableEquipmentService,
            GetParticipantsForEventService participantsService
    ) {
        this.createInterimInvoiceService = createInterimInvoiceService;
        this.invoiceRepository = invoiceRepository;
        this.getRentableEquipmentService = getRentableEquipmentService;
        this.participantsService = participantsService;
    }

    /* =========================================================
       1) INVOICES TAB → Teilnehmerliste mit
          "Issue interim invoice"-Button
       ========================================================= */
    @GetMapping("/event_management/invoices")
    public String showIssueInvoicePage(
            @RequestParam("eventId") Long eventId,
            Model model
    ) {
        List<ParticipantDTO> participants =
                participantsService.getParticipants(eventId);

        model.addAttribute("eventId", eventId);
        model.addAttribute("participants", participants);
        model.addAttribute("activeTab", "invoices");

        return "event_management/issue_invoice";
    }

    /* =========================================================
       2) DETAILSEITE → Rechnungen + Equipment-Auswahl
       ========================================================= */
    @GetMapping("/event_management/invoices/issue")
    public String showInvoicesForBooking(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "created", required = false) Boolean created,
            Model model
    ) {
        model.addAttribute("bookingId", bookingId);
        model.addAttribute("activeTab", "invoices");

        model.addAttribute(
                "invoices",
                invoiceRepository.findByBookingId(bookingId)
        );

        model.addAttribute(
                "services",
                getRentableEquipmentService.getRentableEquipment()
        );

        if (Boolean.TRUE.equals(created)) {
            model.addAttribute(
                    "successMessage",
                    "Interim invoice created successfully."
            );
        }

        return "event_management/invoices";
    }

    /* =========================================================
       3) CREATE INTERIM INVOICE
       ========================================================= */
    @PostMapping("/event_management/invoices/interim")
    public String createInterimInvoice(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "equipmentIds", required = false)
            List<Long> equipmentIds
    ) {
        createInterimInvoiceService.createInterimInvoice(
                bookingId,
                equipmentIds
        );

        return "redirect:/event_management/invoices/issue?bookingId="
                + bookingId + "&created=true";
    }
}
