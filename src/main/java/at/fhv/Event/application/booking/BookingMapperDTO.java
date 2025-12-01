package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;

@Component
public class BookingMapperDTO {

    public BookingDTO toDTO(Booking b) {
        return new BookingDTO(
                b.getId(),
                b.getEventId(),
                b.getBookerFirstName(),
                b.getBookerLastName(),
                b.getBookerEmail(),
                b.getSeats(),
                b.getTotalPrice(),
                b.getStatus(),
                b.getCreatedAt()
        );
    }
}
