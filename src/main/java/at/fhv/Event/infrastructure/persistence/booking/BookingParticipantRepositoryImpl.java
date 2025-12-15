package at.fhv.Event.infrastructure.persistence.booking;

import at.fhv.Event.domain.model.booking.*;
import at.fhv.Event.infrastructure.mapper.BookingParticipantMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class BookingParticipantRepositoryImpl
        implements BookingParticipantRepository {

    private final JpaBookingParticipantRepository jpaRepo;
    private final BookingParticipantMapper mapper;

    public BookingParticipantRepositoryImpl(
            JpaBookingParticipantRepository jpaRepo,
            BookingParticipantMapper mapper
    ) {
        this.jpaRepo = jpaRepo;
        this.mapper = mapper;
    }

    @Override
    public Optional<BookingParticipant> findById(Long id) {
        return jpaRepo.findById(id).map(mapper::toDomain);
    }

    @Override
    public List<BookingParticipant> findByBookingId(Long bookingId) {
        return jpaRepo.findByBookingId(bookingId)
                .stream()
                .map(mapper::toDomain)
                .toList();
    }

    @Override
    public BookingParticipant save(BookingParticipant participant) {

        BookingParticipantEntity entity = jpaRepo.findById(participant.getId())
                .orElseThrow(() -> new RuntimeException("Participant not found"));

        entity.setCheckInStatus(participant.getCheckInStatus());

        return mapper.toDomain(entity);
    }


}

