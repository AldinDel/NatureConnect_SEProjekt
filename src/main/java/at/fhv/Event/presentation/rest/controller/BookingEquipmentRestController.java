package at.fhv.Event.presentation.rest.controller;

import at.fhv.Event.application.equipment.GetEquipmentForBookingService;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.presentation.rest.response.checkout.ReturnedEquipmentDTO;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/events")
public class BookingEquipmentRestController {

    private final GetEquipmentForBookingService service;
    private final EquipmentRepository equipmentRepository;

    public BookingEquipmentRestController(
            GetEquipmentForBookingService service,
            EquipmentRepository equipmentRepository
    ) {
        this.service = service;
        this.equipmentRepository = equipmentRepository;
    }


    @GetMapping("/{eventId}/bookings/{bookingId}/equipment")
    public List<ReturnedEquipmentDTO> getEquipmentForBooking(
            @PathVariable Long bookingId
    ) {
        return service.getForBooking(bookingId)
                .stream()
                .map(eq -> {
                    var equipment = equipmentRepository
                            .findById(eq.getEquipmentId())
                            .orElseThrow();

                    return new ReturnedEquipmentDTO(
                            equipment.getId(),
                            equipment.getName(),
                            eq.getQuantity()
                    );
                })
                .toList();
    }

}
