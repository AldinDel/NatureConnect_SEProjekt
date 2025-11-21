package at.fhv.Event.rest.mapper;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;
@Component
public class BookingResponseMapper {

    public BookingDTO toDTO(Booking b) {
        BookingDTO dto = new BookingDTO();

        dto.setId(b.getId());
        dto.setEventId(b.getEventId());
        dto.setBookerFirstName(b.getBookerFirstName());
        dto.setBookerLastName(b.getBookerLastName());
        dto.setBookerEmail(b.getBookerEmail());
        dto.setSeats(b.getSeats());
        dto.setTotalPrice(b.getTotalPrice());
        dto.setStatus(b.getStatus());

        return dto;
    }
}
