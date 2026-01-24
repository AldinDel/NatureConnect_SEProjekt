package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.feedback.FeedbackService;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.exception.ParticipantNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(FeedbackController.class);
    private final FeedbackService feedbackService;
    private final ErrorMessageService errorMessageService;


    public FeedbackController(FeedbackService feedbackService, ErrorMessageService errorMessageService) {
        this.feedbackService = feedbackService;
        this.errorMessageService = errorMessageService;
    }


    @GetMapping("/feedback/new")
    public String showFeedbackForm(
            @RequestParam Long participantId,
            @RequestParam Long eventId,
            Model model
    ) {
        model.addAttribute("participantId", participantId);
        model.addAttribute("eventId", eventId);
        return "feedback/feedback";
    }


    @PostMapping("/feedback")
    public String saveFeedback(
            @RequestParam Integer rating,
            @RequestParam(required = false) String comment,
            @RequestParam Long eventId,
            @RequestParam Long participantId,
            RedirectAttributes redirectAttributes
    ) {
        try {
            feedbackService.createCustomerFeedback(
                    rating,
                    comment,
                    eventId,
                    participantId,
                    null
            );
            redirectAttributes.addFlashAttribute("feedbackSaved", true);
            return "redirect:/event_management/checkout?eventId=" + eventId;
        } catch (IllegalStateException e) {
            logger.warn("Feedback already exists for participant {}", participantId);
            redirectAttributes.addFlashAttribute("error",
                    "Feedback was already submitted for this participant.");
            return "redirect:/event_management/checkout?eventId=" + eventId;

        } catch (ParticipantNotFoundException e) {
            logger.error("Participant not found: {}", participantId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getParticipantId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/checkout?eventId=" + eventId;

        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to save feedback for participant {}", participantId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management/checkout?eventId=" + eventId;
        }
    }
}
