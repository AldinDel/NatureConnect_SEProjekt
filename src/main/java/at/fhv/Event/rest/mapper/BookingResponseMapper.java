package at.fhv.Event.rest.mapper.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;

@Component
public class BookingResponseMapper {

    public BookingDTO toDTO(Booking booking) {
        BookingDTO dto = new BookingDTO();

        dto.setId(booking.getId());
        dto.setEventId(booking.getEventId());
        dto.setFirstName(booking.getFirstName());
        dto.setLastName(booking.getLastName());
        dto.setEmail(booking.getEmail());
        dto.setSeats(booking.getSeats());
        dto.setTotalPrice(booking.getTotalPrice());
        dto.setStatus(booking.getStatus());

        return dto;
    }
}
