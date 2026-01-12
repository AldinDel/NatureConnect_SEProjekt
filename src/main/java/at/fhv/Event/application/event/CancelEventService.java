package at.fhv.Event.application.event;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.payment.PaymentStatus;
import org.springframework.cache.annotation.CacheEvict;
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

    private void validateEvent(Event event) {
        if (event == null) {
            throw new IllegalStateException("Event not found");
        }

        if (Boolean.TRUE.equals(event.getCancelled())) {
            throw new IllegalStateException("Event is already cancelled.");
        }

        if (event.getDate() == null || event.getStartTime() == null) {
            return;
        }

        LocalDateTime eventStart = LocalDateTime.of(
                event.getDate(),
                event.getStartTime()
        );

        if (eventStart.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("Expired events cannot be cancelled.");
        }
    }


    @Transactional
    @CacheEvict(value = "events", key = "#eventId")
    public void cancel(Long eventId, String reason) {
        if (reason == null || reason.isBlank()) {
            throw new IllegalArgumentException("Cancellation reason must not be empty.");
        }

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        validateEvent(event);

        event.cancel(reason);
        eventRepository.save(event);
        List<Booking> bookings = bookingRepository.findByEventId(eventId);

        for (Booking booking : bookings) {
            if (booking.getStatus() != BookingStatus.CANCELLED) {
                bookingRepository.updateStatus(booking.getId(), BookingStatus.CANCELLED);
            }
        }

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

    public long getRefundableCount(Long eventId) {
        return bookingRepository.findByEventId(eventId).stream()
                .filter(b -> b.getPaymentStatus() == PaymentStatus.PAID)
                .count();
    }

}