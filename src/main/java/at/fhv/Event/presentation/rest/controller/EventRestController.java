package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.checkin.CheckInService;
import at.fhv.Event.application.event.*;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventRestController {

    private final CreateEventService createService;
    private final GetEventDetailsService detailsService;
    private final UpdateEventService updateService;
    private final CancelEventService cancelService;
    private final SearchEventService searchService;
    private final CheckInService checkInService;


    public EventRestController(CreateEventService createService,
                               GetEventDetailsService detailsService,
                               UpdateEventService updateService,
                               CancelEventService cancelService,
                               SearchEventService searchService,
                               CheckInService checkInService) {
        this.createService = createService;
        this.detailsService = detailsService;
        this.updateService = updateService;
        this.cancelService = cancelService;
        this.searchService = searchService;
        this.checkInService = checkInService;
    }

    @GetMapping
    public ResponseEntity<List<EventOverviewDTO>> getAll() {
        return ResponseEntity.ok(searchService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventDetailDTO> get(@PathVariable Long id) {
        return ResponseEntity.ok(detailsService.getEventDetails(id));
    }

    @PostMapping
    public ResponseEntity<EventDetailDTO> create(@RequestBody CreateEventRequest req) {
        return ResponseEntity.ok(createService.createEvent(req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EventDetailDTO> update(@PathVariable Long id,
                                                 @RequestBody UpdateEventRequest req) {
        return ResponseEntity.ok(updateService.updateEvent(id, req));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<Void> cancel(@PathVariable Long id) {
        cancelService.cancel(id);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{id}/equipment")
    public ResponseEntity<List<EquipmentDTO>> getEquipment(@PathVariable Long id) {
        EventDetailDTO event = detailsService.getEventDetails(id);
        return ResponseEntity.ok(event.equipments());
    }

    @PostMapping("/{eventId}/participants/{participantId}/checkin")
    public ResponseEntity<Void> checkIn(
            @PathVariable Long eventId,
            @PathVariable Long participantId) {

        checkInService.checkIn(participantId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{eventId}/participants/{participantId}/notarrived")
    public ResponseEntity<Void> markNotArrived(
            @PathVariable Long eventId,
            @PathVariable Long participantId) {

        checkInService.markNotArrived(participantId);
        return ResponseEntity.ok().build();
    }
    @PostMapping("/{eventId}/participants/{participantId}/reset")
    public ResponseEntity<Void> resetStatus(
            @PathVariable Long eventId,
            @PathVariable Long participantId) {

        checkInService.resetStatus(participantId);
        return ResponseEntity.ok().build();
    }




}