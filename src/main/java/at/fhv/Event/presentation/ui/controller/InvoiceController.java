package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.GetUserBookingsService;
import at.fhv.Event.application.booking.SplitInvoiceService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.invoice.Invoice;
import at.fhv.Event.domain.model.invoice.InvoiceRepository;
import at.fhv.Event.domain.model.invoice.InvoiceStatus;
import at.fhv.Event.presentation.rest.response.booking.BookingWithEventDTO;
import at.fhv.Event.presentation.rest.response.invoice.InvoiceWithEventDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/profile/invoices")
public class InvoiceController {

    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    private final GetUserBookingsService userBookingsService;
    private final GetEventDetailsService eventDetailsService;
    private final SplitInvoiceService splitInvoiceService;
    private final InvoiceRepository invoiceRepository;

    public InvoiceController(
            GetUserBookingsService userBookingsService,
            GetEventDetailsService eventDetailsService,
            SplitInvoiceService splitInvoiceService,
            InvoiceRepository invoiceRepository) {
        this.userBookingsService = userBookingsService;
        this.eventDetailsService = eventDetailsService;
        this.splitInvoiceService = splitInvoiceService;
        this.invoiceRepository = invoiceRepository;
    }

    @GetMapping
    public String invoicesPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            String email = principal.getName();
            List<Booking> userBookings = userBookingsService.getBookingsByUserEmail(email);

            logger.debug("Loading invoices for user: {} - Found {} bookings", email, userBookings.size());

            // Load all invoices for user's bookings
            List<InvoiceWithEventDTO> invoiceDTOs = userBookings.stream()
                    .flatMap(booking -> {
                        List<Invoice> invoices = invoiceRepository.findByBookingId(booking.getId());
                        logger.debug("Booking {} has {} invoices", booking.getId(), invoices.size());
                        return invoices.stream()
                                .filter(invoice -> invoice.getStatus() == InvoiceStatus.FINAL)
                                .map(invoice -> {
                                    logger.debug("Invoice {} with total: {}", invoice.getId(), invoice.getTotal());
                                    return new InvoiceWithEventDTO(
                                            invoice,
                                            eventDetailsService.getEventDetails(booking.getEventId())
                                    );
                                });
                    })
                    .toList();

            // Load open bookings (not fully paid)
            List<BookingWithEventDTO> openBookingDTOs = userBookings.stream()
                    .filter(b -> !b.isFullyPaid() && !b.isCancelled())
                    .map(b -> {
                        logger.debug("Open booking {} - Total: {}, Paid: {}", b.getId(), b.getTotalPrice(), b.getPaidAmount());
                        return new BookingWithEventDTO(
                                b,
                                eventDetailsService.getEventDetails(b.getEventId())
                        );
                    })
                    .toList();

            logger.info("Loaded {} invoices and {} open bookings for user {}", invoiceDTOs.size(), openBookingDTOs.size(), email);

            model.addAttribute("invoices", invoiceDTOs);
            model.addAttribute("openBookings", openBookingDTOs);
            return "profile/my_invoices";
        } catch (Exception e) {
            logger.error("Error loading invoices for user {}: {}", principal.getName(), e.getMessage(), e);
            model.addAttribute("error", "Error loading invoices: " + e.getMessage());
            model.addAttribute("invoices", List.of());
            model.addAttribute("openBookings", List.of());
            return "profile/my_invoices";
        }
    }

    @PostMapping("/{bookingId}/split/fifty-percent")
    public String payFiftyPercent(
            @PathVariable Long bookingId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/profile/invoices";
        }

        logger.info("User {} requested 50% payment for booking {}", principal.getName(), bookingId);

        try {
            splitInvoiceService.payFiftyPercent(bookingId, principal.getName());
            logger.info("50% payment completed successfully for booking {}", bookingId);
            redirectAttributes.addFlashAttribute("success", "50% paid successfully. New invoice created.");
        } catch (Exception e) {
            logger.error("50% payment failed for booking {}: {}", bookingId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/profile/invoices";
    }

    @PostMapping("/{bookingId}/split/equipment")
    public String paySelectedEquipment(
            @PathVariable Long bookingId,
            @RequestParam(required = false) List<Long> equipmentIds,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        logger.info("User {} requested equipment payment for booking {} with equipment IDs: {}",
                principal.getName(), bookingId, equipmentIds);

        try {
            if (equipmentIds == null || equipmentIds.isEmpty()) {
                logger.warn("No equipment selected for booking {}", bookingId);
                redirectAttributes.addFlashAttribute("error", "Please select at least one item to pay");
                return "redirect:/profile/invoices";
            }

            splitInvoiceService.paySelectedEquipment(bookingId, principal.getName(), equipmentIds);
            logger.info("Equipment payment completed successfully for booking {}", bookingId);
            redirectAttributes.addFlashAttribute("success", "Selected equipment paid successfully. New invoice created.");
        } catch (Exception e) {
            logger.error("Equipment payment failed for booking {}: {}", bookingId, e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Error: " + e.getMessage());
        }

        return "redirect:/profile/invoices";
    }

    @PostMapping("/{bookingId}/pay-remaining")
    public String payRemaining(
            @PathVariable Long bookingId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            splitInvoiceService.payRemainingAmount(bookingId, principal.getName());
            redirectAttributes.addFlashAttribute("success", "Invoice fully paid!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/profile/invoices";

    }

    @GetMapping("/view")
    public String viewInvoice(
            @RequestParam Long invoiceId,
            Principal principal,
            Model model) {

        if (principal == null) {
            return "redirect:/login";
        }

        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invoice not found"));

        boolean belongsToUser = userBookingsService
                .getBookingsByUserEmail(principal.getName())
                .stream()
                .anyMatch(b -> b.getId().equals(invoice.getBookingId()));

        if (!belongsToUser) {
            throw new AccessDeniedException("Access denied");
        }

        model.addAttribute("invoice", invoice);
        model.addAttribute("canEditInvoice", false);

        return "event_management/invoice_view";
    }

}
