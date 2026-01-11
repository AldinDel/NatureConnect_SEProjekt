package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.feedback.FeedbackService;
import at.fhv.Event.presentation.ui.dto.FeedbackAdminRow;
import at.fhv.Event.presentation.ui.dto.FeedbackStats;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/event_management/feedback")
public class EventFeedbackController {

    private final FeedbackService feedbackService;

    public EventFeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @GetMapping
    public String showFeedback(
            @RequestParam Long eventId,
            @RequestParam(required = false) String rating,
            @RequestParam(required = false) String comments,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        FeedbackStats stats = feedbackService.getFeedbackStats(eventId);
        List<FeedbackAdminRow> feedbacks = feedbackService.getFeedbackForEvent(eventId,rating,comments,sort);

        model.addAttribute("eventId", eventId);
        model.addAttribute("stats", stats);
        model.addAttribute("feedbacks", feedbacks);
        model.addAttribute("rating", rating);
        model.addAttribute("comments", comments);
        model.addAttribute("sort", sort);

        return "event_management/feedback";
    }
}
