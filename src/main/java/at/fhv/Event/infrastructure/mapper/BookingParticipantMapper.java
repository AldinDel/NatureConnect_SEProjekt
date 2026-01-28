package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.infrastructure.persistence.booking.BookingEntity;
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

    public BookingParticipantEntity toEntity(BookingParticipant p) {
        BookingParticipantEntity e = new BookingParticipantEntity();

        // Booking nur per ID referenzieren (kein DB-Load nötig)
        BookingEntity bookingRef = new BookingEntity();
        bookingRef.setId(p.getBookingId());
        e.setBooking(bookingRef);

        e.setFirstName(p.getFirstName());
        e.setLastName(p.getLastName());
        e.setAge(p.getAge());
        e.setCheckInStatus(p.getCheckInStatus());
        e.setCheckOutStatus(p.getCheckOutStatus());

        return e;
    }


}
