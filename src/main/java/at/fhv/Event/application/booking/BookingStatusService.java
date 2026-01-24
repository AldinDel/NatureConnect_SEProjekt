package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class BookingStatusService {

    public boolean isExpired(EventDetailDTO event) {
        if (event == null) return false;
        if (event.date() == null) return false;
        return event.date().isBefore(LocalDate.now());
    }

    public boolean isInactive(Booking booking, EventDetailDTO event) {
        return booking.isCancelled() || isExpired(event);
    }

    public boolean isCancelled(Booking booking, EventDetailDTO event) {
        return booking.isCancelled();
    }
}
