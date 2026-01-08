package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.checkout.CheckOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/events")
public class CheckOutRestController {

    private final CheckOutService checkOutService;

    public CheckOutRestController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    @PostMapping("/{eventId}/participants/{participantId}/checkout")
    public ResponseEntity<?> checkOut(@PathVariable Long participantId) {
        try {
            checkOutService.checkOut(participantId);
            return ResponseEntity.ok().build();
        } catch (ResponseStatusException ex) {
            return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
        }
    }
}
