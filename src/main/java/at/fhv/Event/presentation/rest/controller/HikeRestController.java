package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.hiking.GetHikeRoutesForEventService;
import at.fhv.Event.application.hiking.HikeRouteDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/hiking")
public class HikeRestController {

    private final GetHikeRoutesForEventService getHikeRoutesForEventService;

    public HikeRestController (GetHikeRoutesForEventService getHikeRoutesForEventService) {
        this.getHikeRoutesForEventService = getHikeRoutesForEventService;
    }

    @GetMapping("/event/{eventId}")
    public List<HikeRouteDTO> getHikesForEvent(@PathVariable Integer eventId) {
        // Mehrere Routen pro Event:
        return getHikeRoutesForEventService.getRoutesForEvent(eventId);
    }
}
