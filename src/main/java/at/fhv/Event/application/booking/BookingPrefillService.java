package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.application.request.booking.ParticipantDTO;
import at.fhv.Event.domain.model.booking.AudienceType;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.user.CustomerProfileRepository;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Service;
import at.fhv.Event.application.request.booking.ParticipantDTO;
import org.springframework.transaction.annotation.Transactional;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BookingPrefillService {

    private final CustomerProfileRepository customerProfileRepository;

    public BookingPrefillService(CustomerProfileRepository repo) {
        this.customerProfileRepository = repo;
        this.voucherService = voucherService;
    }

    @Transactional(readOnly = true)
    public CreateBookingRequest prepareCreateRequestForLoggedInUser(String email, Long eventId) {
        CreateBookingRequest request = new CreateBookingRequest();
        request.setEventId(eventId);
        request.setAudience(AudienceType.INDIVIDUAL);

        customerProfileRepository.findByEmail(email)
                .ifPresent(customer -> {
                    request.setBookerFirstName(customer.getFirstName());
                    request.setBookerLastName(customer.getLastName());
                    request.setBookerEmail(customer.getEmail());

                    ParticipantDTO p1 = new ParticipantDTO();
                    p1.setFirstName(customer.getFirstName());
                    p1.setLastName(customer.getLastName());
                    p1.setAge(null);

                    request.setParticipants(List.of(p1));
                    request.setSeats(1);
                });

        return request;
    }

    public CreateBookingRequest prepareEditRequest(Booking booking, List<EquipmentDTO> availableEquipment) {
        CreateBookingRequest req = new CreateBookingRequest();

        req.setEventId(booking.getEventId());
        req.setBookerFirstName(booking.getBookerFirstName());
        req.setBookerLastName(booking.getBookerLastName());
        req.setBookerEmail(booking.getBookerEmail());
        req.setSeats(booking.getSeats());
        req.setAudience(booking.getAudience());
        req.setVoucherCode(booking.getVoucherCode());
        req.setSpecialNotes(booking.getSpecialNotes());

        enrichWithVoucherDiscount(req, booking.getVoucherCode());
        enrichWithParticipants(req, booking);
        enrichWithEquipment(req, booking, availableEquipment);

        return req;
    }

    private void enrichWithVoucherDiscount(CreateBookingRequest req, String voucherCode) {
        if (voucherCode != null && !voucherCode.isBlank()) {
            var voucherValidation = voucherService.validate(voucherCode);
            if (voucherValidation.valid) {
                req.setDiscountPercent(voucherValidation.discountPercent);
            }
        }
    }

    private void enrichWithParticipants(CreateBookingRequest req, Booking booking) {
        if (booking.getParticipants() != null && !booking.getParticipants().isEmpty()) {
            List<ParticipantDTO> participants = booking.getParticipants().stream().map(p -> {
                ParticipantDTO dto = new ParticipantDTO();
                dto.setFirstName(p.getFirstName());
                dto.setLastName(p.getLastName());
                dto.setAge(p.getAge());
                return dto;
            }).toList();
            req.setParticipants(participants);
        }
    }

    private void enrichWithEquipment(CreateBookingRequest req, Booking booking, List<EquipmentDTO> availableEquipment) {
        Map<Long, EquipmentSelection> equipmentMap = new HashMap<>();

        for (EquipmentDTO equipment : availableEquipment) {
            EquipmentSelection sel = new EquipmentSelection();
            sel.setSelected(false);
            sel.setQuantity(1);
            equipmentMap.put(equipment.id(), sel);
        }

        if (booking.getEquipment() != null) {
            for (BookingEquipment be : booking.getEquipment()) {
                EquipmentSelection sel = new EquipmentSelection();
                sel.setSelected(true);
                sel.setQuantity(be.getQuantity());
                equipmentMap.put(be.getEquipmentId(), sel);
            }
        }

        req.setEquipment(equipmentMap);
    }
}
