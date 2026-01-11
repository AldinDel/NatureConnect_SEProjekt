package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.feedback.FeedbackService;
import at.fhv.Event.domain.model.user.UserAccount;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
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
        } catch (IllegalStateException e) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Feedback was already submitted for this participant."
            );
            return "redirect:/event_management/checkout?eventId=" + eventId;
        }

        redirectAttributes.addFlashAttribute("feedbackSaved", true);
        return "redirect:/event_management/checkout?eventId=" + eventId;
    }

}
