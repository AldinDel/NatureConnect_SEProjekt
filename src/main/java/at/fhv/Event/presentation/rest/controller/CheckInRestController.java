package at.fhv.Event.presentation.rest.controller;


import at.fhv.Event.application.checkin.CheckInService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
public class CheckInRestController {
    private final CheckInService checkInService;

    public CheckInRestController(CheckInService checkInService) {
        this.checkInService = checkInService;
    }

    @PostMapping("/{participantId}/checkin")
    public void checkIn(@PathVariable Long eventId,
                        @PathVariable Long participantId) {
        checkInService.checkIn(participantId);
    }

    @PostMapping("/{participantId}/not-arrived")
    public void notArrived(@PathVariable Long participantId) {
        checkInService.markNotArrived(participantId);
    }

    @PostMapping("/{participantId}/reset")
    public void reset(@PathVariable Long participantId) {
        checkInService.resetStatus(participantId);
    }


}
