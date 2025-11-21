package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.application.request.booking.CreateBookingRequest;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.mapper.BookingMapper;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    public BookingRepositoryImpl(BookingJpaRepository jpa, BookingMapper mapper, EventJpaRepository eventJpa, EventMapper eventMapper) {
        this.jpa = jpa;
        this.mapper = mapper;
        this.eventJpa = eventJpa;
        this.eventMapper = eventMapper;
    }

    @Override
    public Booking save(Booking booking) {
        var entity = mapper.toEntity(booking);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpa.findById(id)
                .map(mapper::toDomain);
    }

    @Override
    public List<Booking> findAll() {
        return jpa.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public List<Booking> findByEventId(Long eventId) {
        return jpa.findByEventId(eventId).stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public int countSeatsForEvent(Long eventId) {
        Integer result = jpa.countConfirmedSeatsForEvent(eventId);
        return result == null ? 0 : result;
    }

    @Override
    public Event loadEventForBooking(Long eventId) {
        return eventJpa.findById(eventId)
                .map(eventMapper::toDomain)   // falls du Mapper hast
                .orElse(null);
    }

    @Override
    public Map<Long, EquipmentEntity> loadEquipmentMap(CreateBookingRequest request) {
        List<Long> ids = request.getEquipment().keySet().stream().toList();

        List<EquipmentEntity> list = equipmentJpa.findAllById(ids);

        return list.stream()
                .collect(Collectors.toMap(
                        EquipmentEntity::getId,
                        e -> e
                ));
    }
}
