package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.GetBookingEquipmentForInvoiceService;
import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.invoice.CreateInterimInvoiceService;
import at.fhv.Event.application.invoice.GenerateInvoicePdfService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.BookingNotFoundException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.exception.InvoiceCreationException;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.infrastructure.persistence.booking.BookingEquipmentJpaRepository;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import at.fhv.Event.presentation.ui.dto.InvoiceServiceDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class InvoicesController {
    private static final Logger logger = LoggerFactory.getLogger(InvoicesController.class);

    private final CreateInterimInvoiceService createInterimInvoiceService;
    private final InvoiceRepository invoiceRepository;
    private final GetBookingEquipmentForInvoiceService bookingEquipmentService;
    private final GetParticipantsForEventService participantsService;
    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final BookingEquipmentJpaRepository bookingEquipmentJpaRepository;
    private final EquipmentRepository equipmentRepository;
    private final GenerateInvoicePdfService generateInvoicePdfService;
    private final ErrorMessageService errorMessageService;



    public InvoicesController(
            CreateInterimInvoiceService createInterimInvoiceService,
            InvoiceRepository invoiceRepository,
            GetBookingEquipmentForInvoiceService bookingEquipmentService,
            GetParticipantsForEventService participantsService,
            BookingRepository bookingRepository,
            EventRepository eventRepository,
            BookingEquipmentJpaRepository bookingEquipmentJpaRepository,
            EquipmentRepository equipmentRepository,
            GenerateInvoicePdfService generateInvoicePdfService,
            ErrorMessageService errorMessageService
    ) {
        this.createInterimInvoiceService = createInterimInvoiceService;
        this.invoiceRepository = invoiceRepository;
        this.bookingEquipmentService = bookingEquipmentService;
        this.participantsService = participantsService;
        this.bookingRepository = bookingRepository;
        this.eventRepository = eventRepository;
        this.bookingEquipmentJpaRepository = bookingEquipmentJpaRepository;
        this.equipmentRepository = equipmentRepository;
        this.generateInvoicePdfService = generateInvoicePdfService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/event_management/invoices")
    public String showIssueInvoicePage(
            @RequestParam("eventId") Long eventId,
            Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            List<ParticipantDTO> participants =
                    participantsService.getParticipants(eventId);

            model.addAttribute("eventId", eventId);
            model.addAttribute("participants", participants);
            model.addAttribute("activeTab", "invoices");

            return "event_management/issue_invoice";
        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to load invoice issue page for event {}", eventId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";
        }
    }

    @GetMapping("/event_management/invoices/issue")
    public String showInvoicesForBooking(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "created", required = false) Boolean created,
            Model model, RedirectAttributes redirectAttributes) {
        try {
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

            List<InvoiceServiceDTO> services =

                    bookingEquipmentJpaRepository
                            .findByBooking_IdAndInvoicedFalse(bookingId)
                            .stream()
                            .map(be -> {
                                Equipment eq = equipmentRepository.findById(be.getEquipmentId())
                                        .orElseThrow();
                                return new InvoiceServiceDTO(
                                        be.getEquipmentId(),
                                        eq.getName(),
                                        be.getPricePerUnit()
                                );
                            })
                            .toList();

            model.addAttribute("services", services);
            model.addAttribute("hasOpenServices", !services.isEmpty());


            if (Boolean.TRUE.equals(created)) {
                model.addAttribute(
                        "successMessage",
                        "Interim invoice created successfully."
                );
            }

            return "event_management/invoices";
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found: {}", bookingId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (EventNotFoundException e) {
            logger.error("Event not found for booking: {}", bookingId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to load invoices for booking {}", bookingId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";
        }
    }


    @GetMapping("/event_management/invoices/view")
    public String viewInvoice(
            @RequestParam("invoiceId") Long invoiceId,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            var invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() ->
                            new RuntimeException("Invoice not found: " + invoiceId)
                    );

            model.addAttribute("invoice", invoice);
            model.addAttribute("canEditInvoice", true);
            return "event_management/invoice_view";
        } catch (IllegalArgumentException e) {
            logger.error("Invoice not found: {}", invoiceId, e);
            redirectAttributes.addFlashAttribute("error", "Invoice not found.");
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to view invoice {}", invoiceId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";
        }
    }

    @GetMapping("/invoices/{id}/download")
    public ResponseEntity<byte[]> downloadInvoice(
            @PathVariable("id") Long invoiceId
    ) {
        try {
            var invoice = invoiceRepository.findById(invoiceId)
                    .orElseThrow(() ->
                            new RuntimeException("Invoice not found: " + invoiceId)
                    );

            byte[] pdf = generateInvoicePdfService.generate(invoice);

            return ResponseEntity.ok()
                    .header(
                            HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=invoice_" + invoiceId + ".pdf"
                    )
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(pdf);
        } catch (Exception e) {
            logger.warn("Failed to download invoice {}", invoiceId, e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/event_management/invoices/interim")
    public String createInterimInvoice(
            @RequestParam("bookingId") Long bookingId,
            @RequestParam(value = "equipmentIds", required = false)
            List<Long> equipmentIds,
            @RequestParam(value = "includeEventPrice", required = false)
            Boolean includeEventPrice,
            RedirectAttributes redirectAttributes
    ) {
        try {
            createInterimInvoiceService.createInterimInvoice(
                    bookingId,
                    equipmentIds,
                    Boolean.TRUE.equals(includeEventPrice)
            );

            return "redirect:/event_management/invoices/issue?bookingId="
                    + bookingId + "&created=true";
        } catch (BookingNotFoundException e) {
            logger.error("Booking not found for interim invoice: {}", bookingId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getBookingId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/invoices/issue?bookingId=" + bookingId;

        } catch (InvoiceCreationException e) {
            logger.error("Invoice creation failed: {}", e.getMessage(), e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getBookingId(), e.getReason());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/invoices/issue?bookingId=" + bookingId;

        } catch (Exception e) {
            logger.error("Unexpected error creating interim invoice for booking {}", bookingId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/invoices/issue?bookingId=" + bookingId;
        }
    }

    @PostMapping("/event_management/invoices/finalize")
    public String finalizeInvoice(
            @RequestParam("invoiceId") Long invoiceId, RedirectAttributes redirectAttributes) {
        try {
            invoiceRepository.finalizeInvoice(invoiceId);
            return "redirect:/event_management/invoices/view?invoiceId=" + invoiceId;
        } catch (IllegalArgumentException e) {
            logger.error("Invoice not found for finalization: {}", invoiceId, e);
            redirectAttributes.addFlashAttribute("error", "Invoice not found.");
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to finalize invoice {}", invoiceId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/invoices/view?invoiceId=" + invoiceId;
        }
    }

}
