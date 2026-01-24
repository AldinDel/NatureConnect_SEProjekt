package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.presentation.rest.response.booking.EventParticipantsStats;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ParticipantsController {

    private static final Logger logger = LoggerFactory.getLogger(ParticipantsController.class);
    private final GetParticipantsForEventService participantsService;
    private final ErrorMessageService errorMessageService;

    public ParticipantsController(GetParticipantsForEventService participantsService, ErrorMessageService errorMessageService) {
        this.participantsService = participantsService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/event_management/participants")
    public String showParticipants(
            @RequestParam("eventId") Long eventId,
            Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            List<ParticipantDTO> participants = participantsService.getParticipants(eventId);
            EventParticipantsStats stats = participantsService.getStatsForEvent(eventId);

            model.addAttribute("participants", participants);
            model.addAttribute("eventId", eventId);

            model.addAttribute("totalCount", stats.getTotal());
            model.addAttribute("arrivedCount", stats.getArrived());
            model.addAttribute("notArrivedCount", stats.getNotArrived());
            model.addAttribute("registeredCount", stats.getRegistered());
            model.addAttribute("activeTab", "checkin");


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
