package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.feedback.FeedbackService;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.presentation.ui.dto.FeedbackAdminRow;
import at.fhv.Event.presentation.ui.dto.FeedbackStats;
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
@RequestMapping("/event_management/feedback")
public class EventFeedbackController {

    private static final Logger logger = LoggerFactory.getLogger(EventFeedbackController.class);
    private final FeedbackService feedbackService;
    private final ErrorMessageService errorMessageService;

    public EventFeedbackController(FeedbackService feedbackService, ErrorMessageService errorMessageService) {
        this.feedbackService = feedbackService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping
    public String showFeedback(
            @RequestParam Long eventId,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String sort,
            Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            FeedbackStats stats = feedbackService.getFeedbackStats(eventId);
            List<FeedbackAdminRow> feedbacks = feedbackService.getFeedbackForEvent(eventId, rating, comments, sort);

            model.addAttribute("eventId", eventId);
            model.addAttribute("stats", stats);
            model.addAttribute("feedbacks", feedbacks);
            model.addAttribute("rating", rating);
            model.addAttribute("comments", comments);
            model.addAttribute("sort", sort);

            return "event_management/feedback";
        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to load feedback for event {}", eventId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";
        }
    }
}
