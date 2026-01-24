package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetEventsForTodayService;
import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/event_management")
public class EventManagementController {
    private static final Logger logger = LoggerFactory.getLogger(EventManagementController.class);

    private final GetEventsForTodayService getEventsForTodayService;
    private final GetParticipantsForEventService getParticipantsForEventService;
    private final ErrorMessageService errorMessageService;

    public EventManagementController(GetEventsForTodayService getEventsForTodayService,
                             GetParticipantsForEventService getParticipantsForEventService, ErrorMessageService errorMessageService) {
        this.getEventsForTodayService = getEventsForTodayService;
        this.getParticipantsForEventService = getParticipantsForEventService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping
    public String showEventsForToday(Model model, RedirectAttributes redirectAttributes) {
        try {
            List<EventOverviewDTO> events = getEventsForTodayService.getEventsForToday();
            model.addAttribute("events", events);
            return "event_management/event_management";
        } catch (Exception e) {
            logger.error("Failed to load today's events", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            model.addAttribute("events", List.of());
            return "event_management/event_management";
        }
    }

    @GetMapping("/{eventId}/participants")
    public String showParticipants(
            @PathVariable Long eventId,
            Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            var participants = getParticipantsForEventService.getParticipants(eventId);

            model.addAttribute("participants", participants);
            model.addAttribute("eventId", eventId);

            return "event_management/participants";
        } catch (EventNotFoundException e) {
            logger.error("Event not found: {}", eventId, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";

        } catch (Exception e) {
            logger.error("Failed to load participants for event {}", eventId, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/event_management";
        }
    }
}
