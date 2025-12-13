package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.presentation.rest.response.booking.EventParticipantsStats;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class ParticipantsController {

    private final GetParticipantsForEventService participantsService;

    public ParticipantsController(GetParticipantsForEventService participantsService) {
        this.participantsService = participantsService;
    }

    @GetMapping("/event_management/participants")
    public String showParticipants(
            @RequestParam("eventId") Long eventId,
            Model model
    ) {
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
    }
}
