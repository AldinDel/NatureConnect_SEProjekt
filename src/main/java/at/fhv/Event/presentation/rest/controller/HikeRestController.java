package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.hiking.GetHikeRoutesForEventService;
import at.fhv.Event.application.hiking.GraphDTO;
import at.fhv.Event.application.hiking.HikeRouteDTO;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Profile("!test")
@RequestMapping("/api/hiking")
public class HikeRestController {

    private final GetHikeRoutesForEventService service;

    public HikeRestController(GetHikeRoutesForEventService service) {
        this.service = service;
    }

    @GetMapping("/routes")
    public List<HikeRouteDTO> getAllRoutes() {
        return service.getAllRoutes();
    }

    @GetMapping("/routes/best")
    public HikeRouteDTO getBestRoute(@RequestParam String filter) {
        return service.getBestRoute(filter);
    }

    @GetMapping("/routes/{key}/graph")
    public GraphDTO getRouteGraph(@PathVariable String key) {
        return service.getGraphForRoute(key);
    }
}
