package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllBookingsService {

    private final BookingRepository bookingRepository;

    public GetAllBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(b -> new BookingDTO(
                        b.getId(),
                        b.getEventId(),
                        b.getCustomerId(),
                        b.isGuest(),
                        b.getFirstName(),
                        b.getLastName(),
                        b.getEmail(),
                        b.getSeats(),
                        b.getStatus(),
                        b.getPaymentMethod(),
                        b.getVoucherCode(),
                        b.getVoucherValue(),
                        b.getUnitPrice(),
                        b.getTotalPrice(),
                        b.getConfirmedAt(),
                        b.getCancelledAt()
                ))
                .toList();
    }
}
