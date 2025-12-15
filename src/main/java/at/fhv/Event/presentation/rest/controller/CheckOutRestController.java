package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.checkout.CheckOutService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events/{eventId}/participants")
public class CheckOutRestController {

    private final CheckOutService checkOutService;

    public CheckOutRestController(CheckOutService checkOutService) {
        this.checkOutService = checkOutService;
    }

    @PostMapping("/{participantId}/checkout")
    public ResponseEntity<Void> checkOut(@PathVariable Long participantId) {
        checkOutService.checkOut(participantId);
        return ResponseEntity.ok().build();
    }
}
