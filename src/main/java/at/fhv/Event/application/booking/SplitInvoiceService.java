package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.exception.PaymentOperationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SplitInvoiceService {

    private final BookingRepository bookingRepository;

    public SplitInvoiceService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void payFiftyPercent(Long bookingId, String userEmail) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.isPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        booking.payFiftyPercent();
        bookingRepository.save(booking);
    }

    @Transactional
    public void paySelectedEquipment(Long bookingId, String userEmail, List<Long> equipmentIds) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.isPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        booking.payEquipmentItems(equipmentIds);
        bookingRepository.save(booking);
    }

    @Transactional
    public void payRemainingAmount(Long bookingId, String userEmail) {
        Booking booking = getBookingForUser(bookingId, userEmail);

        if (booking.isPaid()) {
            throw new PaymentOperationException(bookingId, "Booking is already fully paid");
        }

        double remaining = booking.getRemainingAmount();
        if (remaining > 0) {
            booking.makePartialPayment(remaining);
            bookingRepository.save(booking);
        }
    }

    private Booking getBookingForUser(Long bookingId, String userEmail) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found"));

        if (!booking.getBookerEmail().equalsIgnoreCase(userEmail)) {
            throw new PaymentOperationException(bookingId, "You can only manage your own bookings");
        }

        return booking;
    }
}
