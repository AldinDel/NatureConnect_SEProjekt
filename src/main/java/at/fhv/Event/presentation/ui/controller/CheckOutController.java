package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.ParticipantCheckInStatus;
import at.fhv.Event.presentation.rest.response.booking.EventCheckoutStats;
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

    private final GetParticipantsForEventService participantsService;

    public CheckOutController(GetParticipantsForEventService participantsService) {
        this.participantsService = participantsService;
    }

    @GetMapping
    public String showCheckout(
            @RequestParam Long eventId,
            Model model
    ) {
        model.addAttribute("eventId", eventId);
        model.addAttribute("activeTab", "checkout");

        List<ParticipantDTO> participants =
                participantsService.getParticipants(eventId).stream()
                        .filter(p -> p.getCheckInStatus() == ParticipantCheckInStatus.CHECKED_IN)
                        .toList();

        long total = participants.size();

        long checkedOut = participants.stream()
                .filter(ParticipantDTO::isCheckedOut)
                .count();

        long remaining = total - checkedOut;

        model.addAttribute("participants", participants);
        model.addAttribute("totalCount", total);
        model.addAttribute("checkedOutCount", checkedOut);
        model.addAttribute("remainingCount", remaining);


        return "event_management/checkout";
    }

}
