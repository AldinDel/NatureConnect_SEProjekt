package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class BookingMapperDTO {

    private final EventRepository eventRepository;

    public BookingMapperDTO(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public BookingDTO toDTO(Booking b) {
        var event = eventRepository.findById(b.getEventId())
                .orElseThrow();

        LocalDate eventDate = event.getDate(); // oder getStartDate() je nach Modell
        LocalDate today = LocalDate.now();

        boolean expired = !eventDate.isAfter(today);

        return new BookingDTO(
                b.getId(),
                b.getEventId(),
                b.getBookerFirstName(),
                b.getBookerLastName(),
                b.getBookerEmail(),
                b.getSeats(),
                b.getTotalPrice(),
                b.getStatus(),
                b.getCreatedAt(),
                expired
        );
    }
}
