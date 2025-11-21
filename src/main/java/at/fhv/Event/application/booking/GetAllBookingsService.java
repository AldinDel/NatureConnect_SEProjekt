package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetAllBookingsService {

    private final BookingRepository bookingRepository;
    private final BookingMapperDTO mapper;
    public GetAllBookingsService(BookingRepository bookingRepository, BookingMapperDTO mapper) {
        this.bookingRepository = bookingRepository;
        this.mapper = mapper;
    }

    public List<BookingDTO> getAllBookings() {
        return bookingRepository.findAll().stream()
                .map(mapper::toDTO)
                .toList();
    }
}
