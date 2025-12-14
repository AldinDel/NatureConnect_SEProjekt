package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.booking.GetUserBookingsService;
import at.fhv.Event.application.booking.SplitInvoiceService;
import at.fhv.Event.application.event.GetEventDetailsService;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.presentation.rest.response.booking.BookingWithEventDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;

@Controller
@RequestMapping("/invoices")
public class InvoiceController {

    private final GetUserBookingsService userBookingsService;
    private final GetEventDetailsService eventDetailsService;
    private final SplitInvoiceService splitInvoiceService;

    public InvoiceController(
            GetUserBookingsService userBookingsService,
            GetEventDetailsService eventDetailsService,
            SplitInvoiceService splitInvoiceService) {
        this.userBookingsService = userBookingsService;
        this.eventDetailsService = eventDetailsService;
        this.splitInvoiceService = splitInvoiceService;
    }

    @GetMapping
    public String invoicesPage(Model model, Principal principal) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            String email = principal.getName();
            List<Booking> userBookings = userBookingsService.getBookingsByUserEmail(email);

            List<BookingWithEventDTO> bookingDTOs = userBookings.stream()
                    .map(b -> new BookingWithEventDTO(
                            b,
                            eventDetailsService.getEventDetails(b.getEventId())
                    ))
                    .toList();

            model.addAttribute("bookings", bookingDTOs);
            return "invoice/my_invoices";
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Error loading invoices: " + e.getMessage());
            model.addAttribute("bookings", List.of());
            return "invoice/my_invoices";
        }
    }

    @PostMapping("/{bookingId}/split/fifty-percent")
    public String payFiftyPercent(
            @PathVariable Long bookingId,
            Principal principal,
            RedirectAttributes redirectAttributes) {

        if (principal == null) {
            return "redirect:/login";
        }

        try {
            splitInvoiceService.payFiftyPercent(bookingId, principal.getName());
            redirectAttributes.addFlashAttribute("success", "50% paid successfully. Remaining amount due on event day.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/invoices";
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

        try {
            if (equipmentIds == null || equipmentIds.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "Please select at least one item to pay");
                return "redirect:/invoices";
            }

            splitInvoiceService.paySelectedEquipment(bookingId, principal.getName(), equipmentIds);
            redirectAttributes.addFlashAttribute("success", "Selected items paid successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/invoices";
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

        return "redirect:/invoices";
    }
}
