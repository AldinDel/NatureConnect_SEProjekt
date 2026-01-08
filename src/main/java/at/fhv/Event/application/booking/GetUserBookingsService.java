package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetUserBookingsService {
    private final BookingRepository bookingRepository;

    public GetUserBookingsService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional(readOnly = true)
    public List<Booking> getBookingsByUserEmail(String email) {
        List<Booking> bookings = bookingRepository.findByCustomerEmail(email);
        bookings.forEach(b -> {
            b.getEquipment().size();
            b.getParticipants().size();
        });
        return bookings;
    }


}
