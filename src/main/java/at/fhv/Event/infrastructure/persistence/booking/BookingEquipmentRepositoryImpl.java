package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingEquipmentRepository;
import at.fhv.Event.infrastructure.mapper.BookingEquipmentMapper;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class BookingEquipmentRepositoryImpl implements BookingEquipmentRepository {
    private final BookingEquipmentJpaRepository jpaRepository;
    private final BookingEquipmentMapper mapper;

    public BookingEquipmentRepositoryImpl(BookingEquipmentJpaRepository jpaRepository, BookingEquipmentMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<BookingEquipment> findNotYetInvoicedByBookingId(Long bookingId) {
        List<BookingEquipmentEntity> entities = jpaRepository.findNotYetInvoicedByBookingId(bookingId);

        List<BookingEquipment> result = new ArrayList<>();
        for (BookingEquipmentEntity entity : entities) {
            BookingEquipment domain = mapper.toDomain(entity);
            result.add(domain);
        }

        return result;
    }

    @Override
    public List<BookingEquipment> findByBookingId(Long bookingId) {
        return jpaRepository.findByBooking_Id(bookingId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public Optional<BookingEquipment> findById(Long id) {
        Optional<BookingEquipmentEntity> entityOpt = jpaRepository.findById(id);

        if (entityOpt.isPresent()) {
            return Optional.of(mapper.toDomain(entityOpt.get()));
        }

        return Optional.empty();
    }

    @Override
    public BookingEquipment save(BookingEquipment bookingEquipment, BookingEntity bookingEntity) {
        BookingEquipmentEntity entity = mapper.toEntity(bookingEquipment, bookingEntity);
        BookingEquipmentEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public List<BookingEquipment> saveAll(List<BookingEquipment> equipment, BookingEntity bookingEntity) {
        List<BookingEquipmentEntity> entities = new ArrayList<>();

        for (BookingEquipment eq : equipment) {
            entities.add(mapper.toEntity(eq, bookingEntity));
        }

        List<BookingEquipmentEntity> savedEntities = jpaRepository.saveAll(entities);

        return savedEntities.stream()
                .map(mapper::toDomain)
                .toList();
    }


}
