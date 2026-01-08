package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.GetBookingEquipmentForInvoiceService;
import at.fhv.Event.application.invoice.CreateInterimInvoiceService;
import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
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
    private final GetBookingEquipmentForInvoiceService bookingEquipmentService;
    private final GetParticipantsForEventService participantsService;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;

    public InvoicesController(
            CreateInterimInvoiceService createInterimInvoiceService,
            InvoiceRepository invoiceRepository,
            GetBookingEquipmentForInvoiceService bookingEquipmentService,
            GetParticipantsForEventService participantsService,
            BookingRepository bookingRepository,
            EventRepository eventRepository
    ) {
        this.createInterimInvoiceService = createInterimInvoiceService;
        this.invoiceRepository = invoiceRepository;
        this.bookingEquipmentService = bookingEquipmentService;
        this.participantsService = participantsService;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
    }

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

    @GetMapping("/event_management/invoices/issue")
    public String showInvoicesForBooking(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "created", required = false) Boolean created,
            Model model
    ) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow();

        model.addAttribute("billingReady", booking.isBillingReady());

        boolean eventPriceAlreadyInvoiced =
                invoiceRepository.existsEventPriceForBooking(bookingId);

        Event event = eventRepository.findById(booking.getEventId())
                .orElseThrow();

        model.addAttribute("bookingId", bookingId);
        model.addAttribute("activeTab", "invoices");
        model.addAttribute("eventPrice", event.getPrice());
        model.addAttribute("includeEventPrice", !eventPriceAlreadyInvoiced);

        model.addAttribute(
                "invoices",
                invoiceRepository.findByBookingId(bookingId)
        );

        model.addAttribute(
                "services",
                bookingEquipmentService.getEquipmentUsedSoFar(bookingId)
        );

        if (Boolean.TRUE.equals(created)) {
            model.addAttribute(
                    "successMessage",
                    "Interim invoice created successfully."
            );
        }

        return "event_management/invoices";
    }

    @GetMapping("/event_management/invoices/view")
    public String viewInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            Model model
    ) {
        var invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() ->
                        new RuntimeException("Invoice not found: " + invoiceId)
                );

        model.addAttribute("invoice", invoice);
        return "event_management/invoice_view";
    }

    @PostMapping("/event_management/invoices/interim")
    public String createInterimInvoice(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "equipmentIds", required = false)
            List<Long> equipmentIds,
            @RequestParam(value = "includeEventPrice", required = false)
            Boolean includeEventPrice
    ) {
        createInterimInvoiceService.createInterimInvoice(
                bookingId,
                equipmentIds,
                Boolean.TRUE.equals(includeEventPrice)
        );

        return "redirect:/event_management/invoices/issue?bookingId="
                + bookingId + "&created=true";
    }

    @PostMapping("/event_management/invoices/finalize")
    public String finalizeInvoice(
            @RequestParam("invoiceId") Long invoiceId
    ) {
        invoiceRepository.finalizeInvoice(invoiceId);
        return "redirect:/event_management/invoices/view?invoiceId=" + invoiceId;
    }

}
