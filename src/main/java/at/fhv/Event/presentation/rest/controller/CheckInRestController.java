package at.fhv.Event.presentation.rest.controller;


import at.fhv.Event.application.checkin.CheckInService;
import at.fhv.Event.presentation.rest.request.WalkInBookingRequest;
import at.fhv.Event.presentation.rest.response.booking.BookingEquipmentDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @PostMapping("/walkin")
    public void walkInCheckIn(@PathVariable Long eventId,
                              @RequestBody WalkInBookingRequest request) {
        checkInService.createWalkInBooking(eventId, request);
    }

    @GetMapping("/{bookingId}/has-equipment")
    public boolean bookingHasEquipment(@PathVariable Long bookingId) {
        return checkInService.bookingHasEquipment(bookingId);
    }

    @GetMapping("/{bookingId}/equipment")
    public List<BookingEquipmentDTO> getBookingEquipment(
            @PathVariable Long eventId,
            @PathVariable Long bookingId
    ) {
        return checkInService.getBookingEquipment(bookingId);
    }

    @PostMapping("/{participantId}/checkout-with-equipment")
    public void checkoutWithEquipment(
            @PathVariable Long eventId,
            @PathVariable Long participantId,
            @RequestParam Long bookingId
    ) {
        checkInService.returnEquipmentAndCheckout(participantId, bookingId);
    }

}
