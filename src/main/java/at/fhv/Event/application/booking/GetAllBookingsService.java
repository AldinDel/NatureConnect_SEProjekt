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
                        b.getEvent().getId(),
                        b.getCustomerName(),
                        b.getNumberOfParticipants(),
                        b.getDate()
                ))
                .toList();
    }
}
