package at.fhv.Event.application.booking;

import at.fhv.Event.application.refund.RefundService;
import at.fhv.Event.application.request.booking.BookingRequestMapper;
import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.application.request.booking.ParticipantDTO;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.equipment.EquipmentRepository;
import at.fhv.Event.domain.model.equipment.EquipmentSelection;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.exception.*;
import at.fhv.Event.domain.model.payment.PaymentMethod;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
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
    private final RefundService refundService;

    public BookEventService(
            BookingRepository bookingRepository,
            EquipmentRepository equipmentRepository,
            BookingRequestMapper bookingRequestMapper,
            BookingMapperDTO bookingMapperDTO,
            BookingValidator bookingValidator,
            RefundService refundService) {

        _bookingRepository = bookingRepository;
        _equipmentRepository = equipmentRepository;
        _bookingRequestMapper = bookingRequestMapper;
        _bookingMapperDTO = bookingMapperDTO;
        _bookingValidator = bookingValidator;
        this.refundService = refundService;
    }

    // -----------------------------------------------------------------------
    // BOOKING CREATION
    // -----------------------------------------------------------------------

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
        booking.setEquipment(processEquipmentBooking(request, equipmentMap));

        Booking savedBooking = _bookingRepository.save(booking);

        return _bookingMapperDTO.toDTO(savedBooking);
    }

    public BookingDTO getDTOById(Long bookingId) {
        return _bookingMapperDTO.toDTO(findBookingById(bookingId));
    }

    public Booking getById(Long bookingId) {
        return findBookingById(bookingId);
    }

    public void assertEventIsEditableForBooking(Booking booking) {
        Event event = loadEvent(booking.getEventId());
        checkEventAvailability(event);
    }

    // -----------------------------------------------------------------------
    // BOOKING UPDATE
    // -----------------------------------------------------------------------

    @Transactional
    public BookingDTO updateBooking(Long bookingId, CreateBookingRequest request) {

        Booking booking = findBookingById(bookingId);
        Event event = loadEvent(request.getEventId());

        checkEventAvailability(event);
        checkEventCapacityForUpdate(event, booking, request.getSeats());

        Map<Long, Equipment> equipmentMap = loadEquipmentMap(request);

        int alreadyBooked = _bookingRepository.countOccupiedSeatsForEvent(event.getId());
        int alreadyBookedExcludingThis = Math.max(0, alreadyBooked - booking.getSeats());

        List<ValidationError> errors =
                _bookingValidator.validate(request, event, equipmentMap, alreadyBookedExcludingThis);

        if (!errors.isEmpty()) {
            throw new BookingValidationException(errors);
        }

        normalizeVoucherCode(request);

        BigDecimal totalPrice = calculateTotalPrice(request, event, equipmentMap);
        BigDecimal discount = calculateDiscount(totalPrice, request);

        booking.setSeats(request.getSeats());
        booking.setAudience(request.getAudience());
        booking.setBookerFirstName(request.getBookerFirstName());
        booking.setBookerLastName(request.getBookerLastName());
        booking.setBookerEmail(request.getBookerEmail());
        booking.setVoucherCode(request.getVoucherCode());
        booking.setSpecialNotes(request.getSpecialNotes());
        booking.setDiscountAmount(discount.doubleValue());
        booking.setTotalPrice(totalPrice.doubleValue());

        // Update participants
        List<BookingParticipant> participants = new ArrayList<>();
        if (request.getParticipants() != null) {
            for (ParticipantDTO p : request.getParticipants()) {
                participants.add(new BookingParticipant(
                        p.getFirstName(),
                        p.getLastName(),
                        p.getAge()
                ));
            }
        }
        booking.setParticipants(participants);

        // Update equipment
        List<BookingEquipment> equipmentList = new ArrayList<>();
        if (request.getEquipment() != null) {
            for (var entry : request.getEquipment().entrySet()) {

                Long equipmentId = entry.getKey();
                EquipmentSelection sel = entry.getValue();

                if (!sel.isSelected() || sel.getQuantity() <= 0)
                    continue;

                Equipment equipment = equipmentMap.get(equipmentId);
                if (equipment == null)
                    continue;

                equipmentList.add(new BookingEquipment(
                        equipment.getId(),
                        sel.getQuantity(),
                        equipment.getUnitPrice().doubleValue()
                ));
            }
        }

        booking.setEquipment(equipmentList);

        Booking savedBooking = _bookingRepository.save(booking);
        return _bookingMapperDTO.toDTO(savedBooking);
    }

    // -----------------------------------------------------------------------
    // BOOKING CANCELLATION
    // -----------------------------------------------------------------------

    // WICHTIG – alte Signatur wiederhergestellt, um 500-Fehler zu vermeiden
    @Transactional
    public void cancelBooking(Long bookingId, String email) {
        cancelBooking(bookingId, email, false);
    }

    @Transactional
    public void cancelBooking(Long bookingId, String email, boolean isAdmin) {

        Booking booking = getById(bookingId);

        // Customer darf NUR eigene Buchungen stornieren
        if (!isAdmin && !booking.getBookerEmail().equalsIgnoreCase(email)) {
            throw new IllegalStateException("You cannot cancel someone else's booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new IllegalStateException("This booking is already cancelled.");
        }

        Event event = loadEvent(booking.getEventId());
        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());

        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This event already started and cannot be cancelled.");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        _bookingRepository.save(booking);

        refundService.processRefund(
                booking.getBookerEmail(),
                booking.getId(),
                booking.getTotalPrice()
        );
    }

    // -----------------------------------------------------------------------
    // PAYMENT
    // -----------------------------------------------------------------------

    @Transactional
    public BookingDTO updatePaymentMethod(Long bookingId, String paymentMethodName) {

        Booking booking = findBookingById(bookingId);
        PaymentMethod paymentMethod = parsePaymentMethod(bookingId, paymentMethodName);

        booking.setPaymentMethod(paymentMethod);
        booking.setPaymentStatus(PaymentStatus.PAID);
        booking.setStatus(BookingStatus.CONFIRMED);

        return _bookingMapperDTO.toDTO(_bookingRepository.save(booking));
    }

    // -----------------------------------------------------------------------
    // INTERNAL HELPERS
    // -----------------------------------------------------------------------

    private Event loadEvent(Long eventId) {
        try {
            return _bookingRepository.loadEventForBooking(eventId);
        } catch (Exception e) {
            throw new EventNotFoundException(eventId);
        }
    }

    private void checkEventAvailability(Event event) {
        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new IllegalStateException("This event is cancelled and cannot be booked.");
        }
        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("This event is expired and cannot be booked.");
        }
    }

    private void checkEventCapacity(Event event, int requestedSeats) {

        int max = event.getMaxParticipants();
        int min = event.getMinParticipants() == null ? 0 : event.getMinParticipants();

        int confirmed = _bookingRepository.countOccupiedSeatsForEvent(event.getId());

        int remaining = (max - min) - confirmed;

        if (remaining <= 0)
            throw new EventFullyBookedException(event.getId(), requestedSeats, 0);

        if (requestedSeats > remaining)
            throw new EventFullyBookedException(event.getId(), requestedSeats, remaining);
    }

    private void checkEventCapacityForUpdate(Event event, Booking booking, int newSeats) {

        int confirmed = _bookingRepository.countOccupiedSeatsForEvent(event.getId());
        int excludingThis = Math.max(0, confirmed - booking.getSeats());

        int available = event.getMaxParticipants() - event.getMinParticipants();
        int remaining = available - excludingThis;

        if (remaining <= 0)
            throw new EventFullyBookedException(event.getId(), newSeats, 0);

        if (newSeats > remaining)
            throw new EventFullyBookedException(event.getId(), newSeats, remaining);
    }

    private Map<Long, Equipment> loadEquipmentMap(CreateBookingRequest request) {
        if (request.getEquipment() == null || request.getEquipment().isEmpty())
            return Map.of();

        List<Long> ids = new ArrayList<>(request.getEquipment().keySet());
        return _equipmentRepository.findByIds(ids);
    }

    private void validateBookingRequest(CreateBookingRequest request,
                                        Event event,
                                        Map<Long, Equipment> equipmentMap) {

        int booked = _bookingRepository.countOccupiedSeatsForEvent(event.getId());
        List<ValidationError> errors =
                _bookingValidator.validate(request, event, equipmentMap, booked);

        if (!errors.isEmpty())
            throw new BookingValidationException(errors);
    }

    private void normalizeVoucherCode(CreateBookingRequest req) {
        if (req.getVoucherCode() != null && req.getVoucherCode().isBlank())
            req.setVoucherCode(null);
    }

    private BigDecimal calculateTotalPrice(CreateBookingRequest request,
                                           Event event,
                                           Map<Long, Equipment> equipmentMap) {

        BigDecimal base = event.getPrice()
                .multiply(BigDecimal.valueOf(request.getSeats()));

        BigDecimal equipmentPrice = calculateEquipmentPrice(request, equipmentMap);
        BigDecimal subtotal = base.add(equipmentPrice);

        return subtotal.subtract(calculateDiscount(subtotal, request));
    }

    private BigDecimal calculateEquipmentPrice(CreateBookingRequest req,
                                               Map<Long, Equipment> map) {

        BigDecimal total = BigDecimal.ZERO;

        for (var entry : req.getEquipment().entrySet()) {
            EquipmentSelection sel = entry.getValue();

            if (!sel.isSelected())
                continue;

            Equipment equipment = map.get(entry.getKey());
            if (equipment == null)
                continue;

            total = total.add(
                    equipment.getUnitPrice()
                            .multiply(BigDecimal.valueOf(sel.getQuantity()))
            );
        }
        return total;
    }

    private BigDecimal calculateDiscount(BigDecimal subtotal,
                                         CreateBookingRequest req) {

        if (req.getDiscountPercent() == null)
            return BigDecimal.ZERO;

        BigDecimal rate =
                BigDecimal.valueOf(req.getDiscountPercent()).divide(BigDecimal.valueOf(100));

        return subtotal.multiply(rate);
    }

    private Booking createBooking(CreateBookingRequest req, BigDecimal totalPrice) {

        Booking booking = _bookingRequestMapper.toDomain(req, totalPrice.doubleValue());

        BigDecimal discount = calculateDiscount(totalPrice, req);

        booking.setDiscountAmount(discount.doubleValue());
        booking.setTotalPrice(totalPrice.doubleValue());

        return booking;
    }

    private List<BookingEquipment> processEquipmentBooking(
            CreateBookingRequest req,
            Map<Long, Equipment> map) {

        List<BookingEquipment> list = new ArrayList<>();

        for (var entry : req.getEquipment().entrySet()) {

            EquipmentSelection sel = entry.getValue();

            if (!sel.isSelected() || sel.getQuantity() <= 0)
                continue;

            Equipment equipment = map.get(entry.getKey());
            if (equipment == null)
                continue;

            reduceEquipmentStock(equipment, sel.getQuantity());

            list.add(new BookingEquipment(
                    equipment.getId(),
                    sel.getQuantity(),
                    equipment.getUnitPrice().doubleValue()
            ));
        }
        return list;
    }

    private void reduceEquipmentStock(Equipment equipment, int qty) {

        if (!equipment.hasEnoughStock(qty)) {
            throw new InsufficientStockException(
                    equipment.getId(),
                    equipment.getName(),
                    qty,
                    equipment.getStock()
            );
        }

        equipment.reduceStock(qty);
        _equipmentRepository.save(equipment);
    }

    private Booking findBookingById(long id) {
        return _bookingRepository.findById(id)
                .orElseThrow(() -> new BookingNotFoundException(id));
    }

    private PaymentMethod parsePaymentMethod(Long bookingId, String name) {

        try {
            return PaymentMethod.valueOf(name);
        } catch (IllegalArgumentException e) {
            throw new PaymentProcessingException(
                    bookingId,
                    name,
                    "Invalid payment method: " + name
            );
        }
    }

    public int getAvailableSeats(Long eventId) {

        Event event = loadEvent(eventId);
        int confirmed = _bookingRepository.countOccupiedSeatsForEvent(eventId);

        int available =
                event.getMaxParticipants() - event.getMinParticipants() - confirmed;

        return Math.max(0, available);
    }

    @Transactional
    public void markAsPaid(Long bookingId) {
        Booking booking = getById(bookingId);
        booking.setStatus(BookingStatus.PAID);
        _bookingRepository.save(booking);
    }

    @Transactional
    public void markAsFailed(Long bookingId) {
        Booking booking = getById(bookingId);
        booking.setStatus(BookingStatus.PAYMENT_FAILED);
        _bookingRepository.save(booking);
    }
}
