package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.presentation.rest.response.booking.BookingDTO;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Component
public class BookingMapperDTO {

    private final EventRepository eventRepository;

    public BookingMapperDTO(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public BookingDTO toDTO(Booking b) {
        var event = eventRepository.findById(b.getEventId())
                .orElseThrow();

        LocalDate dateToUse =
                b.getEventDate() != null
                        ? b.getEventDate()
                        : event.getDate();

        LocalTime endTime = event.getEndTime();

        boolean expired = false;
        if (dateToUse != null && endTime != null) {
            LocalDateTime endDateTime = LocalDateTime.of(dateToUse, endTime);
            expired = endDateTime.isBefore(LocalDateTime.now());
        }

        return new BookingDTO(
                b.getId(),
                b.getEventId(),
                b.getBookerFirstName(),
                b.getBookerLastName(),
                b.getBookerEmail(),
                b.getBookerAddress(),
                b.getSeats(),
                b.getTotalPrice(),
                b.getStatus(),
                b.getCreatedAt(),
                expired
        );
    }
}
