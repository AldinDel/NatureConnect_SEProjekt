package at.fhv.Event.rest.controller;

import at.fhv.Event.application.booking.BookEventService;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin
public class BookingRestController {

    private final BookEventService bookEventService;
    private final EventRepository eventRepository;
    private final EquipmentJpaRepository equipmentJpaRepository;

    public BookingRestController(
            BookEventService bookEventService,
            EventRepository eventRepository,
            EquipmentJpaRepository equipmentJpaRepository
    ) {
        this.bookEventService = bookEventService;
        this.eventRepository = eventRepository;
        this.equipmentJpaRepository = equipmentJpaRepository;
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody CreateBookingRequest request) {
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        Map<Long, EquipmentEntity> equipmentMap = equipmentJpaRepository.findAll()
                .stream()
                .collect(Collectors.toMap(EquipmentEntity::getId, e -> e));

        List<String> errors = bookEventService.validateBooking(request, event, equipmentMap);

        if (!errors.isEmpty()) {
            return ResponseEntity.badRequest().body(errors);
        }

        BookingDTO dto = bookEventService.bookEvent(request);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/{id}")
    public BookingDTO getById(@PathVariable Long id) {
        return bookEventService.getDTOById(id);
    }

    @GetMapping("/payment-methods")
    public ResponseEntity<List<String>> getMethods() {
        return ResponseEntity.ok(
                Arrays.stream(PaymentMethod.values())
                        .map(Enum::name)
                        .toList()
        );
    }
}
