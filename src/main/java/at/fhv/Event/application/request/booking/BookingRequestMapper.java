package at.fhv.Event.application.request.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingParticipant;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.booking.ParticipantStatus;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class BookingRequestMapper {

    public Booking toDomain(CreateBookingRequest req, double basePrice) {

        return new Booking(
                req.getEventId(),
                req.getBookerFirstName(),
                req.getBookerLastName(),
                req.getBookerEmail(),
                req.getSeats(),
                req.getAudience(),
                BookingStatus.PENDING,
                PaymentStatus.UNPAID,
                null,
                req.getVoucherCode(),
                0.0,
                basePrice,
                req.getSpecialNotes(),
                req.getParticipants() != null
                        ? req.getParticipants().stream()
                        .map(p -> new BookingParticipant(
                                null,
                                p.getFirstName(),
                                p.getLastName(),
                                p.getAge(),
                                ParticipantStatus.REGISTERED
                        ))
                        .toList()
                        : List.of(),
                List.of()
        );
    }
}
