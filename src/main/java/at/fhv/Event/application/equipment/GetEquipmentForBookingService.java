package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.booking.BookingEquipment;
import at.fhv.Event.domain.model.booking.BookingEquipmentRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GetEquipmentForBookingService {

    private final BookingEquipmentRepository bookingEquipmentRepository;

    public GetEquipmentForBookingService(
            BookingEquipmentRepository bookingEquipmentRepository
    ) {
        this.bookingEquipmentRepository = bookingEquipmentRepository;
    }

    public List<BookingEquipment> getForBooking(Long bookingId) {
        return bookingEquipmentRepository.findByBookingId(bookingId);
    }
}
