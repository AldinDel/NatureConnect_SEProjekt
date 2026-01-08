package at.fhv.Event.application.request.booking;

import at.fhv.Event.domain.model.booking.*;
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
                                null,
                                p.getFirstName(),
                                p.getLastName(),
                                p.getAge(),
                                ParticipantCheckInStatus.REGISTERED,
                                ParticipantCheckOutStatus.NOT_CHECKED_OUT,
                                null
                        ))
                        .toList()
                        : List.of(),
                List.of()
        );
    }
}
