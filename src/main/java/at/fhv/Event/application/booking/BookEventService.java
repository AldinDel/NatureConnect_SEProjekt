package at.fhv.Event.application.booking;

import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.exception.*;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BookEventService {
    private final BookingRepository _bookingRepository;
    private final EquipmentRepository _equipmentRepository;
    private final BookingRequestMapper _bookingRequestMapper;
    private final BookingMapperDTO _bookingMapperDTO;
    private final BookingValidator _bookingValidator;

    public BookEventService(BookingRepository bookingRepository, EquipmentRepository equipmentRepository, BookingRequestMapper bookingRequestMapper, BookingMapperDTO bookingMapperDTO, BookingValidator bookingValidator) {
        _bookingRepository = bookingRepository;
        _equipmentRepository = equipmentRepository;
        _bookingRequestMapper = bookingRequestMapper;
        _bookingMapperDTO = bookingMapperDTO;
        _bookingValidator = bookingValidator;
    }

    @Transactional
    public BookingDTO bookEvent(CreateBookingRequest request) {
        Event event = loadEvent(request.getEventId());
        checkEventAvailability(event);
        checkEventCapacity(event, request.getSeats());
        Map<Long, Equipment> equipmentMap = loadEquipmentMap(request);
        validateBookingRequest(request, event, equipmentMap);
        normalizeVoucherCode(request);

        BigDecimal totalPrice = calculateTotalPrice(request, event, equipmentMap);
        Booking booking = createBooking(request, totalPrice);
        List<BookingEquipment> equipment = processEquipmentBooking(request, equipmentMap);
        booking.setEquipment(equipment);

        Booking savedBooking = _bookingRepository.save(booking);
        return _bookingMapperDTO.toDTO(savedBooking);
    }

    public BookingDTO getDTOById(Long bookingId) {
        Booking booking = findBookingById(bookingId);
        return _bookingMapperDTO.toDTO(booking);
    }

    public Booking getById(Long bookingId) {
        return findBookingById(bookingId);
    }

    @Transactional
    public BookingDTO updatePaymentMethod(Long bookingId, String paymentMethodName) {
        Booking booking = findBookingById(bookingId);
        PaymentMethod paymentMethod = parsePaymentMethod(bookingId, paymentMethodName);
        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);
        Booking savedBooking = _bookingRepository.save(booking);
        return _bookingMapperDTO.toDTO(savedBooking);
    }

    private Event loadEvent(Long eventId) {
        try {
            return _bookingRepository.loadEventForBooking(eventId);
        } catch (Exception e) {
            throw new EventNotFoundException(eventId);
        }
    }

    private void checkEventAvailability(Event event) {
        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new IllegalStateException("Can't book a cancelled event");
        }
        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Can't book an expired event");
        }
    }

    private void checkEventCapacity(Event event, int requestedSeats) {
        int confirmedSeats = _bookingRepository.countSeatsForEvent(event.getId());
        int availableSeats = event.getMaxParticipants() - event.getMinParticipants();
        int remainingSeats = availableSeats - confirmedSeats;

        if (remainingSeats <= 0) {
            throw new EventFullyBookedException(event.getId(), requestedSeats, 0);
        }
        if (requestedSeats > remainingSeats) {
            throw new EventFullyBookedException(event.getId(), requestedSeats, remainingSeats);
        }
    }

    private Map<Long, Equipment> loadEquipmentMap(CreateBookingRequest request) {
        List<Long> equipmentIds = new ArrayList<>(request.getEquipment().keySet());
        return _equipmentRepository.findByIds(equipmentIds);
    }

    private void validateBookingRequest(CreateBookingRequest request, Event event, Map<Long, Equipment> equipmentMap) {
        int alreadyBooked = _bookingRepository.countSeatsForEvent(event.getId());
        List<ValidationError> errors = _bookingValidator.validate(request, event, equipmentMap, alreadyBooked);
        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }
    }

    private void normalizeVoucherCode(CreateBookingRequest request) {
        if (request.getVoucherCode() != null && request.getVoucherCode().isBlank()) {
            request.setVoucherCode(null);
        }
    }

    private BigDecimal calculateTotalPrice(CreateBookingRequest request, Event event, Map<Long, Equipment> equipmentMap) {
        BigDecimal basePrice = calculateBasePrice(event, request.getSeats());
        BigDecimal equipmentPrice = calculateEquipmentPrice(request, equipmentMap);
        BigDecimal subtotal = basePrice.add(equipmentPrice);
        BigDecimal discount = calculateDiscount(subtotal, request);
        return subtotal.subtract(discount);
    }

    private BigDecimal calculateBasePrice(Event event, int seats) {
        return event.getPrice().multiply(BigDecimal.valueOf(seats));
    }

    private BigDecimal calculateEquipmentPrice(CreateBookingRequest request, Map<Long, Equipment> equipmentMap) {
        BigDecimal total = BigDecimal.ZERO;
        for (var entry : request.getEquipment().entrySet()) {
            EquipmentSelection selection = entry.getValue();

            if (!selection.isSelected()) {
                continue;
            }
            Equipment equipment = equipmentMap.get(entry.getKey());
            if (equipment == null) {
                continue;
            }

            BigDecimal itemTotal = equipment.getUnitPrice().multiply(BigDecimal.valueOf(selection.getQuantity()));
            total = total.add(itemTotal);
        }
        return total;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal, CreateBookingRequest request) {
        if (request.getDiscountPercent() == null) {
            return BigDecimal.ZERO;
        }
        BigDecimal discountRate = BigDecimal.valueOf(request.getDiscountPercent()).divide(BigDecimal.valueOf(100));
        return subtotal.multiply(discountRate);
    }

    private Booking createBooking(CreateBookingRequest request, BigDecimal totalPrice) {
        Booking booking = _bookingRequestMapper.toDomain(request, totalPrice.doubleValue());
        BigDecimal discount = calculateDiscount(totalPrice, request);
        booking.setDiscountAmount(discount.doubleValue());
        booking.setTotalPrice(totalPrice.doubleValue());
        return booking;
    }

    private List<BookingEquipment> processEquipmentBooking(CreateBookingRequest request, Map<Long, Equipment> equipmentMap) {
        List<BookingEquipment> bookingEquipmentList = new ArrayList<>();

        for (var entry : request.getEquipment().entrySet()) {
            EquipmentSelection selection = entry.getValue();
            if (!selection.isSelected() || selection.getQuantity() <= 0) {
                continue;
            }

            Equipment equipment = equipmentMap.get(entry.getKey());
            if (equipment == null) {
                continue;
            }

            reduceEquipmentStock(equipment, selection.getQuantity());
            BookingEquipment bookingEquipment = new BookingEquipment(
                    equipment.getId(),
                    selection.getQuantity(),
                    equipment.getUnitPrice().doubleValue()
            );
            bookingEquipmentList.add(bookingEquipment);
        }
        return bookingEquipmentList;
    }

    private void reduceEquipmentStock(Equipment equipment, int quantity) {
        if (!equipment.hasEnoughStock(quantity)) {
            throw new InsufficientStockException(
                    equipment.getId(),
                    equipment.getName(),
                    quantity,
                    equipment.getStock()
            );
        }
        equipment.reduceStock(quantity);
        _equipmentRepository.save(equipment);
    }

    private Booking findBookingById(long bookingId) {
        return _bookingRepository.findById(bookingId).orElseThrow(() -> new BookingNotFoundException(bookingId));
    }

    private PaymentMethod parsePaymentMethod(Long bookingId, String paymentMethodName) {
        try {
            return PaymentMethod.valueOf(paymentMethodName);
        } catch (IllegalArgumentException e) {
            throw new PaymentProcessingException(
                    bookingId,
                    paymentMethodName,
                    "Invalid payment method: " + paymentMethodName
            );
        }
    }
}
