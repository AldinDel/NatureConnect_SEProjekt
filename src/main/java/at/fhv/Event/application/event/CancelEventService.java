package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventAlreadyCancelledException;
import at.fhv.Event.domain.model.exception.EventDateInPastException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.BookingStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CancelEventService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public CancelEventService(EventRepository eventRepository,
                              BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    public void cancel(Long eventId) {

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        validateEvent(eventId, event);

        // Event canceln
        event.setCancelled(true);
        eventRepository.save(event);

        // Bookings canceln (ohne Equipment löschen)
        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                bookingRepository.updateStatus(booking.getId(), BookingStatus.CANCELLED);
            }
        }
    }

    private void validateEvent(Long eventId, Event event) {

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new EventAlreadyCancelledException(eventId);
        }

        LocalDateTime eventStart = LocalDateTime.of(event.getDate(), event.getStartTime());
        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new EventDateInPastException(eventId, event.getDate());
        }
    }
}
