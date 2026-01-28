package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.equipment.GetEquipmentForEventService;
import at.fhv.Event.application.event.GetParticipantsForEventService;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.EventRepository;
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
    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final GetEquipmentForEventService getEquipmentForEventService;




    public ParticipantsController(
            GetParticipantsForEventService participantsService,
            EventRepository eventRepository,
            BookingRepository bookingRepository,
            GetEquipmentForEventService getEquipmentForEventService
    ) {
        this.participantsService = participantsService;
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.getEquipmentForEventService = getEquipmentForEventService;
    }


    @GetMapping("/event_management/participants")
    public String showParticipants(
            @RequestParam("eventId") Long eventId,
            Model model
    ) {
        List<ParticipantDTO> participants = participantsService.getParticipants(eventId);
        EventParticipantsStats stats = participantsService.getStatsForEvent(eventId);
        int remainingSpots = participantsService.getRemainingSpots(eventId);

        model.addAttribute("participants", participants);
        model.addAttribute("eventId", eventId);
        model.addAttribute("remainingSpots", remainingSpots);
        model.addAttribute("equipment", getEquipmentForEventService.getForEvent(eventId));


        model.addAttribute("totalCount", stats.getTotal());
        model.addAttribute("arrivedCount", stats.getArrived());
        model.addAttribute("notArrivedCount", stats.getNotArrived());
        model.addAttribute("registeredCount", stats.getRegistered());
        model.addAttribute("activeTab", "checkin");

        return "event_management/participants";
    }

}
