package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.mapper.BookingMapper;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository jpa;
    private final BookingMapper mapper;
    private final EventJpaRepository eventJpa;
    private final EventMapper eventMapper;

    @Autowired
    private EquipmentJpaRepository equipmentJpa;

    public BookingRepositoryImpl(BookingJpaRepository jpa, BookingMapper mapper,
                                 EventJpaRepository eventJpa, EventMapper eventMapper) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.eventJpa = eventJpa;
        this.eventMapper = eventMapper;
    }

    @Override
    public Booking save(Booking booking) {

        if (booking.getId() != null) {
            jpa.deleteEquipmentByBookingId(booking.getId());
        }

        var entity = mapper.toEntity(booking);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    // ---------------- EXPIRATION LOGIC ----------------

    private void syncExpiredIfNeeded(Booking booking) {

        Event event = loadEventForBooking(booking.getEventId());
        if (event == null) return;

        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        boolean eventInPast = eventStart.isBefore(LocalDateTime.now());

        if (eventInPast &&
                booking.getStatus() != BookingStatus.CANCELLED &&
                booking.getStatus() != BookingStatus.EXPIRED) {

            // update DB
            jpa.updateStatus(booking.getId(), BookingStatus.EXPIRED);

            // update returned domain object
            booking.setStatus(BookingStatus.EXPIRED);
        }
    }


    private Booking withExpirationSync(Booking booking) {
        syncExpiredIfNeeded(booking);
        return booking;
    }

    // ---------------- FIND METHODS ----------------

    @Override
    public Optional<Booking> findById(Long id) {
        return jpa.findById(id)
                .map(mapper::toDomain)
                .map(this::withExpirationSync);
    }

    @Override
    public List<Booking> findAll() {
        return jpa.findAll().stream()
                .map(mapper::toDomain)
                .map(this::withExpirationSync)
                .toList();
    }

    @Override
    public List<Booking> findByEventId(Long eventId) {
        return jpa.findByEventId(eventId).stream()
                .map(mapper::toDomain)
                .map(this::withExpirationSync)
                .toList();
    }

    @Override
    public List<Booking> findByCustomerEmail(String email) {
        return jpa.findByBookerEmail(email).stream()
                .map(mapper::toDomain)
                .map(this::withExpirationSync)
                .toList();
    }

    // ---------------- EXPIRED MASS UPDATE ----------------

    @Override
    public void markExpiredForEvent(Long eventId) {
        jpa.markExpiredForEvent(eventId);
    }

    // ---------------- OTHER METHODS ----------------

    @Override
    public int countSeatsForEvent(Long eventId) {
        return jpa.countOccupiedSeatsForEvent(eventId);
    }

    @Override
    public Event loadEventForBooking(Long eventId) {
        return eventJpa.findById(eventId)
                .map(eventMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Map<Long, EquipmentEntity> loadEquipmentMap(CreateBookingRequest request) {
        EventEntity eventEntity = eventJpa.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found"));

        List<Long> allowedIds = eventEntity.getEventEquipments().stream()
                .map(ee -> ee.getEquipment().getId())
                .toList();

        return equipmentJpa.findAllById(allowedIds).stream()
                .filter(eq -> eq.getStock() > 0)
                .collect(Collectors.toMap(EquipmentEntity::getId, e -> e));
    }

    @Override
    public int countOccupiedSeatsForEvent(Long eventId) {
        return jpa.countOccupiedSeatsForEvent(eventId);
    }

    @Override
    public void updateStatus(Long id, BookingStatus status) {
        jpa.updateStatus(id, status);
    }
}
