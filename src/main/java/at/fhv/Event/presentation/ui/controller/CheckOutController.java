package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.feedback.FeedbackService;
import at.fhv.Event.domain.model.booking.ParticipantCheckInStatus;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.presentation.ui.dto.CheckoutParticipant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/event_management/checkout")
public class CheckOutController {

    private static final Logger logger = LoggerFactory.getLogger(CheckOutController.class);
    private final GetParticipantsForEventService participantsService;
    private final FeedbackService feedbackService;
    private final ErrorMessageService errorMessageService;

    public CheckOutController(GetParticipantsForEventService participantsService, FeedbackService feedbackService, ErrorMessageService errorMessageService) {
        this.participantsService = participantsService;
        this.feedbackService = feedbackService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping
    public String showCheckout(
            @RequestParam Long eventId,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("eventId", eventId);
            model.addAttribute("activeTab", "checkout");

            List<CheckoutParticipant> participants =
                    participantsService.getParticipants(eventId).stream()
                            .filter(p -> p.getCheckInStatus() == ParticipantCheckInStatus.CHECKED_IN)
                            .map(p -> {
                                CheckoutParticipant dto = new CheckoutParticipant();

                                dto.setParticipantId(p.getParticipantId());
                                dto.setBookingId(p.getBookingId());
                                dto.setBookerName(p.getBookerName());
                                dto.setParticipantName(p.getParticipantName());
                                dto.setParticipantAge(p.getParticipantAge());
                                dto.setCheckedOut(p.isCheckedOut());


                                dto.setFeedbackExists(
                                        feedbackService.feedbackExists(p.getParticipantId())
                                );

                                return dto;
                            })
                            .toList();


            long total = participants.size();

            long checkedOut = participants.stream()
                    .filter(CheckoutParticipant::isCheckedOut)
                    .count();

            long remaining = total - checkedOut;

            model.addAttribute("participants", participants);
            model.addAttribute("totalCount", total);
            model.addAttribute("checkedOutCount", checkedOut);
            model.addAttribute("remainingCount", remaining);


            return "event_management/checkout";
        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (Exception e) {
            logger.error("Failed to load checkout page for event {}", eventId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";
        }
    }

}
