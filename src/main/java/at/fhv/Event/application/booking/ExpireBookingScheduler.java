package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExpireBookingScheduler {

    private final EventJpaRepository eventJpa;
    private final BookingRepository bookingRepository;

    public ExpireBookingScheduler(EventJpaRepository eventJpa,
                                  BookingRepository bookingRepository) {
        this.eventJpa = eventJpa;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    @Scheduled(fixedRate = 10000) // alle 10 Sekunden – nur zum Testen
    // für Production dann: @Scheduled(cron = "0 0 0 * * *")
    public void expireBookings() {

        var today = LocalDate.now();

        // Events direkt aus JPA laden, nicht via Domain-Mapper (vermeidet Lazy-Probleme)
        var events = eventJpa.findAll();

        events.stream()
                // Event-Datum existiert + Event ist heute oder in der Vergangenheit
                .filter(e -> e.getDate() != null && !e.getDate().isAfter(today))
                .forEach(e -> {
                    bookingRepository.markExpiredForEvent(e.getId());
                    System.out.println("[SCHEDULER] Marked bookings expired for event " + e.getId());
                });
    }
}
