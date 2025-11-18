package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;

@Component
public class BookingMapperDTO {

    public BookingDTO toDTO(Booking booking) {
        return new BookingDTO(
                booking.getId(),
                booking.getEventId(),
                booking.getCustomerId(),
                booking.isGuest(),
                booking.getFirstName(),
                booking.getLastName(),
                booking.getEmail(),
                booking.getSeats(),
                booking.getStatus(),
                booking.getPaymentMethod(),
                booking.getVoucherCode(),
                booking.getVoucherValue(),
                booking.getUnitPrice(),
                booking.getTotalPrice(),
                booking.getConfirmedAt(),
                booking.getCancelledAt()
        );

    }
}
