package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.hiking.GetHikeRoutesForEventService;
import at.fhv.Event.application.hiking.HikeRouteDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("!test")
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

    @GetMapping("/event/{eventId}/best")
    public HikeRouteDTO getBestRoute(
            @PathVariable Integer eventId,
            @RequestParam String filter
    ) {
        return getHikeRoutesForEventService.getBestRouteForEvent(eventId, filter);
    }
}
