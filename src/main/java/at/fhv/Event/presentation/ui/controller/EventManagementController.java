package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.application.event.GetEventsForTodayService;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/event_management")
public class EventManagementController {

    private final GetEventsForTodayService getEventsForTodayService;
    private final GetParticipantsForEventService getParticipantsForEventService;

    public EventManagementController(GetEventsForTodayService getEventsForTodayService,
                             GetParticipantsForEventService getParticipantsForEventService) {
        this.getEventsForTodayService = getEventsForTodayService;
        this.getParticipantsForEventService = getParticipantsForEventService;
    }

    @GetMapping
    public String showEventsForToday(Model model) {
        List<EventOverviewDTO> events = getEventsForTodayService.getEventsForToday();
        model.addAttribute("events", events);
        return "event_management/event_management";
    }

    @GetMapping("/{eventId}/participants")
    public String showParticipants(
            @PathVariable Long eventId,
            Model model
    ) {
        var participants = getParticipantsForEventService.getParticipants(eventId);

        model.addAttribute("participants", participants);
        model.addAttribute("eventId", eventId);

        return "event_management/participants";
    }
}
