package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetUserBookingsService {
    private final BookingRepository bookingRepository;

    public GetUserBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<Booking> getBookingsByUserEmail(String email) {
        return bookingRepository.findByCustomerEmail(email);
    }


}
