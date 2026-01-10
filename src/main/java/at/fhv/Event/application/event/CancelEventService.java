package at.fhv.Event.application.event;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventAlreadyCancelledException;
import at.fhv.Event.domain.model.exception.EventDateInPastException;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.BookingStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CancelEventService {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;
    private final AuditLogService auditLogService;

    public CancelEventService(EventRepository eventRepository,
                              BookingRepository bookingRepository,
                              AuditLogService auditLogService) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
        this.auditLogService = auditLogService;
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

        // Audit log
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String userEmail = auth.getName();
            auditLogService.log(
                    userEmail,
                    ActionType.CANCEL,
                    "Cancelled event: " + event.getTitle(),
                    "Event",
                    eventId,
                    bookings.size() + " booking(s) cancelled"
            );
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
