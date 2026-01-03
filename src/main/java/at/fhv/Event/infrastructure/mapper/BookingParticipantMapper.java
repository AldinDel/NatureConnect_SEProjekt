package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.infrastructure.persistence.booking.BookingParticipantEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingParticipantMapper {

    public BookingParticipant toDomain(BookingParticipantEntity e) {
        return new BookingParticipant(
                e.getId(),
                e.getBooking() != null ? e.getBooking().getId() : null,
                e.getFirstName(),
                e.getLastName(),
                e.getAge(),
                e.getCheckInStatus(),
                e.getCheckOutStatus(),
                e.getCheckOutTime()
        );
    }
}
