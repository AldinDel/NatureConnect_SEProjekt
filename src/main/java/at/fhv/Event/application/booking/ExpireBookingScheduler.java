package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.event.EventRepository;
import at.fhv.Event.domain.model.booking.BookingRepository;
import jakarta.transaction.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ExpireBookingScheduler {

    private final EventRepository eventRepository;
    private final BookingRepository bookingRepository;

    public ExpireBookingScheduler(EventRepository eventRepository,
                                  BookingRepository bookingRepository) {
        this.eventRepository = eventRepository;
        this.bookingRepository = bookingRepository;
    }

    @Transactional
    //läuft täglich um 00:00 Uhr
    @Scheduled(cron = "0 0 0 * * *")
    //@Scheduled(fixedRate = 10000) --> alle 10 Sekunden, wäre nur zum Testen
    public void expireBookings() {

        var today = LocalDate.now();
        var events = eventRepository.findAll();

        events.stream()
                .filter(e -> e.getDate() != null && !e.getDate().isAfter(today))
                .forEach(e -> {
                    bookingRepository.markExpiredForEvent(e.getId());
                    System.out.println("[SCHEDULER] Marked expired for event " + e.getId());
                });
    }
}
