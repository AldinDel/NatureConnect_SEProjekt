package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class BookEventService {

    private final BookingRepository bookingRepository;
    private final BookingRequestMapper bookingRequestMapper;
    private final BookingMapperDTO bookingMapperDTO;

    public BookEventService(
            BookingRepository bookingRepository,
            BookingRequestMapper bookingRequestMapper,
            BookingMapperDTO bookingMapperDTO
    ) {
        this.bookingRepository = bookingRepository;
        this.bookingRequestMapper = bookingRequestMapper;
        this.bookingMapperDTO = bookingMapperDTO;
    }

    public BookingDTO bookEvent(CreateBookingRequest request) {
        Event event = bookingRepository.loadEventForBooking(request.getEventId());
        int confirmed = bookingRepository.countSeatsForEvent(event.getId());
        int baseSlots = event.getMaxParticipants() - event.getMinParticipants();
        int remaining = baseSlots - confirmed;

        if (remaining <= 0) {
            throw new IllegalArgumentException("This event is fully booked.");
        }

        if (request.getSeats() > remaining) {
            throw new IllegalArgumentException("Only " + remaining + " spots are remaining for this event.");
        }
        Map<Long, EquipmentEntity> equipmentMap = bookingRepository.loadEquipmentMap(request);

        List<String> errors = validateBooking(request, event, equipmentMap);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join(" | ", errors));
        }

        if (request.getSeats() <= 0) {
            throw new IllegalArgumentException("Seats must be > 0.");
        }

        if (request.getBookerEmail() == null || !request.getBookerEmail().contains("@")) {
            throw new IllegalArgumentException("A valid email is required.");
        }

        if (request.getVoucherCode() != null && request.getVoucherCode().isBlank()) {
            request.setVoucherCode(null);
        }

        BigDecimal basePrice = event.getPrice()
                .multiply(BigDecimal.valueOf(request.getSeats()));

        BigDecimal addons = calculateAddonsTotal(request, equipmentMap);
        BigDecimal subtotal = basePrice.add(addons);
        BigDecimal discount = calculateDiscount(subtotal, request);
        BigDecimal total = subtotal.subtract(discount);
        Booking booking = bookingRequestMapper.toDomain(request, total.doubleValue());

        List<BookingEquipment> equipmentList = new ArrayList<>();

        for (Map.Entry<Long, EquipmentSelection> entry : request.getEquipment().entrySet()) {
            Long eqId = entry.getKey();
            EquipmentSelection chosen = entry.getValue();

            if (!chosen.isSelected() || chosen.getQuantity() <= 0) {
                continue;
            }
            EquipmentEntity eq = equipmentMap.get(eqId);
            if (eq == null) continue;

            BookingEquipment be = new BookingEquipment(
                    eq.getId(),
                    chosen.getQuantity(),
                    eq.getUnitPrice().doubleValue()
            );
            equipmentList.add(be);
        }
        booking.setEquipment(equipmentList);

        booking.setDiscountAmount(discount.doubleValue());
        booking.setTotalPrice(total.doubleValue());

        Booking saved = bookingRepository.save(booking);
        return bookingMapperDTO.toDTO(saved);
    }

    public List<String> validateBooking(CreateBookingRequest req, Event event, Map<Long, EquipmentEntity> equipmentMap) {
        List<String> errors = new ArrayList<>();

        if (req.getBookerFirstName() == null || req.getBookerFirstName().isBlank()) {
            errors.add("First name is required.");
        } else if (req.getBookerFirstName().length() > 50) {
            errors.add("First name cannot exceed 50 characters.");
        }

        if (req.getBookerLastName() == null || req.getBookerLastName().isBlank()) {
            errors.add("Last name is required.");
        } else if (req.getBookerLastName().length() > 50) {
            errors.add("Last name cannot exceed 50 characters.");
        }

        if (req.getBookerEmail() == null || req.getBookerEmail().isBlank()) {
            errors.add("Email is required.");
        } else if (!req.getBookerEmail().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            errors.add("Email format is invalid.");
        } else if (req.getBookerEmail().length() > 100) {
            errors.add("Email cannot exceed 100 characters.");
        }

        if (req.getSeats() < 1) {
            errors.add("At least 1 participant is required.");
        }
        int alreadyBooked = bookingRepository.countSeatsForEvent(event.getId());
        int remaining = event.getMaxParticipants() - alreadyBooked;

        if (req.getSeats() > remaining) {
            errors.add("Only " + remaining + " spots are remaining for this event.");
        }

        if (req.getSeats() > event.getMaxParticipants()) {
            errors.add("Participants exceed maximum allowed for this event.");
        }

        if (req.getParticipants() != null) {
            for (int i = 0; i < req.getParticipants().size(); i++) {
                var p = req.getParticipants().get(i);

                if (p.getFirstName() == null || p.getFirstName().isBlank()) {
                    errors.add("Participant " + (i + 1) + ": First name is required.");
                } else if (p.getFirstName().length() > 50) {
                    errors.add("Participant " + (i + 1) + ": First name too long.");
                }

                if (p.getLastName() == null || p.getLastName().isBlank()) {
                    errors.add("Participant " + (i + 1) + ": Last name is required.");
                } else if (p.getLastName().length() > 50) {
                    errors.add("Participant " + (i + 1) + ": Last name too long.");
                }

                if (p.getAge() < 1 || p.getAge() > 120) {
                    errors.add("Participant " + (i + 1) + ": Age must be between 1 and 120.");
                }
            }
        }

        if (req.getSpecialNotes() != null && req.getSpecialNotes().length() > 250) {
            errors.add("Special notes cannot exceed 250 characters.");
        }

        if (!req.getBookerFirstName().matches("^[A-Za-zÄÖÜäöüß\\- ]+$")) {
            errors.add("First name can only contain letters and '-'.");
        }

        if (!req.getBookerLastName().matches("^[A-Za-zÄÖÜäöüß\\- ]+$")) {
            errors.add("Last name can only contain letters and '-'.");
        }


        if (req.getVoucherCode() != null && req.getVoucherCode().length() > 50) {
            errors.add("Voucher code cannot exceed 50 characters.");
        }
        Map<Long, EventEquipment> eventEqMap = event.getEventEquipments()
                .stream()
                .collect(Collectors.toMap(
                        ee -> ee.getEquipment().getId(),
                        ee -> ee
                ));

        for (var entry : req.getEquipment().entrySet()) {

            Long equipmentId = entry.getKey();
            EquipmentSelection chosen = entry.getValue();

            if (!chosen.isSelected()) continue;

            EventEquipment ee = eventEqMap.get(equipmentId);
            if (ee == null) {
                errors.add("Equipment " + equipmentId + " is not available for this event.");
                continue;
            }

            boolean required = ee.isRequired();
            int stock = ee.getEquipment().getStock();
            int qty = chosen.getQuantity();
            int participants = req.getSeats();

            if (!required) {
                if (qty > stock) {
                    errors.add(ee.getEquipment().getName()
                            + ": maximum available is " + stock);
                }
            }

            if (required) {
                if (participants > stock) {
                    errors.add(ee.getEquipment().getName()
                            + ": requires " + participants + " but only "
                            + stock + " available.");
                }

                if (qty != participants) {
                    errors.add(ee.getEquipment().getName()
                            + " must be booked for all participants (" + participants + ")");
                }
            }
        }
        return errors;
    }

    public BookingDTO getDTOById(Long id) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        return bookingMapperDTO.toDTO(booking);
    }

    public Booking getById(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));
    }

    private BigDecimal calculateAddonsTotal(
            CreateBookingRequest req,
            Map<Long, EquipmentEntity> equipmentMap
    ) {
        BigDecimal total = BigDecimal.ZERO;

        for (var entry : req.getEquipment().entrySet()) {
            Long id = entry.getKey();
            var chosen = entry.getValue();

            if (!chosen.isSelected()) continue;

            EquipmentEntity eq = equipmentMap.get(id);
            if (eq == null) continue;

            BigDecimal price = eq.getUnitPrice();
            int qty = chosen.getQuantity();

            total = total.add(price.multiply(BigDecimal.valueOf(qty)));
        }

        return total;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, CreateBookingRequest req) {
        if (req.getDiscountPercent() == null) {
            return BigDecimal.ZERO;
        }

        BigDecimal percent = BigDecimal.valueOf(req.getDiscountPercent())
                .divide(BigDecimal.valueOf(100));

        return subtotal.multiply(percent);
    }

    public BookingDTO updatePaymentMethod(Long bookingId, String paymentMethod) {
        Booking booking = getById(bookingId);

        booking.setPaymentMethod(PaymentMethod.valueOf(paymentMethod));
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);

        Booking saved = bookingRepository.save(booking);
        return bookingMapperDTO.toDTO(saved);
    }

}
