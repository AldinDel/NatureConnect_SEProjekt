package at.fhv.Event.application.request.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.booking.PaymentMethod;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.OffsetDateTime;

@Component
public class BookingRequestMapper {

    public Booking toDomain(CreateBookingRequest req, double unitPrice, double totalPrice) {

        // Create booking using your existing constructor
        Booking booking = new Booking(
                req.getEventId(),
                req.getCustomerId(), // null if guest
                req.isGuest(),
                req.getFirstName(),
                req.getLastName(),
                req.getEmail(),
                req.getSeats(),
                req.getAudience(),
                BookingStatus.CONFIRMED,  // MVP: confirmed immediately
                PaymentMethod.INVOICE,    // placeholder until payment module is done
                req.getVoucherCode(),
                0.0,                      // voucherValue (calculated later)
                unitPrice,
                totalPrice,
                OffsetDateTime.now(),     // confirmedAt
                null                      // cancelledAt
        );

        // Set audit timestamps (DB will override if triggers exist)
        booking.setCreatedAt(Instant.now());
        booking.setUpdatedAt(Instant.now());

        return booking;
    }
}
