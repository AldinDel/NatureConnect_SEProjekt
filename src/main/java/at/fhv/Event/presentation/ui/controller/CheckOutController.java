package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.ParticipantStatus;
import at.fhv.Event.presentation.rest.response.booking.ParticipantDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/event_management/checkout")
public class CheckOutController {

    private final GetParticipantsForEventService getParticipantsForEventService;

    public CheckOutController(GetParticipantsForEventService service) {
        this.getParticipantsForEventService = service;
    }

    @GetMapping
    public String showCheckout(
            @RequestParam Long eventId,
            Model model
    ) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "checkout");

        List<ParticipantDTO> participants =
                getParticipantsForEventService.getParticipants(eventId)
                        .stream()
                        .filter(p -> p.getCheckInStatus() == ParticipantStatus.CHECKED_IN)
                        .toList();

        long total = participants.size();

        model.addAttribute("participants", participants);
        model.addAttribute("totalCount", total);
        model.addAttribute("checkedOutCount", 0);
        model.addAttribute("remainingCount", total);

        return "event_management/checkout";
    }
}

