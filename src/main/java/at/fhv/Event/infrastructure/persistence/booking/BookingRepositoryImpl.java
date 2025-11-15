package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.infrastructure.mapper.BookingMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookingRepositoryImpl implements BookingRepository {

    private final BookingJpaRepository jpa;
    private final BookingMapper mapper;

    public BookingRepositoryImpl(BookingJpaRepository jpa, BookingMapper mapper) {
        this.jpa = jpa;
        this.mapper = mapper;
    }

    @Override
    public Booking save(Booking booking) {
        var entity = mapper.toEntity(booking);
        var saved = jpa.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Booking> findById(Long id) {
        return jpa.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<Booking> findAll() {
        return jpa.findAll().stream()
                .map(mapper::toDomain)
                .toList();
    }
}
