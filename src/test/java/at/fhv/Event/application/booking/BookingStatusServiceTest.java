package at.fhv.Event.application.booking;

import at.fhv.Event.domain.model.booking.Booking;
import at.fhv.Event.domain.model.booking.BookingStatus;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

// 1) isExpired: true, wenn Event-Datum vor dem heutigen Datum liegt
// 2) isExpired: false, wenn Event-Datum heute ist, Events am heutigen Tag sind noch gültig
// 3) isExpired: false, wenn Event-Datum in der Zukunft liegt
// 4) isInactive: true, wenn Booking den Status CANCELLED hat
// 5) isInactive: true, wenn Event abgelaufen ist (Booking nicht cancelled)
// 6) isInactive: false, wenn Booking aktiv ist und Event nicht abgelaufen ist
// 7) isCancelled: true, wenn Booking den Status CANCELLED hat
// 8) isCancelled: false, wenn Booking nicht CANCELLED ist


class BookingStatusServiceTest {

    private final BookingStatusService service = new BookingStatusService();

    private EventDetailDTO eventWithDate(LocalDate date) {
        return new EventDetailDTO(
                null,
                null,
                null,
                null,
                null,
                date,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                false,
                null,
                null,
                null
        );
    }

    @Test
    void isExpired_shouldReturnTrue_whenEventDateIsBeforeToday() {
        // GIVEN: Event gestern
        EventDetailDTO event = eventWithDate(LocalDate.now().minusDays(1));

        // WHEN
        boolean expired = service.isExpired(event);

        // THEN
        assertTrue(expired);
    }

    @Test
    void isExpired_shouldReturnFalse_whenEventDateIsToday() {
        // GIVEN: event heute
        EventDetailDTO event = eventWithDate(LocalDate.now());

        // WHEN
        boolean expired = service.isExpired(event);

        // THEN
        assertFalse(expired);
    }

    @Test
    void isExpired_shouldReturnFalse_whenEventDateIsInFuture() {
        // GIVEN: event morgen
        EventDetailDTO event = eventWithDate(LocalDate.now().plusDays(1));

        // WHEN
        boolean expired = service.isExpired(event);

        // THEN
        assertFalse(expired);
    }

    @Test
    void isInactive_shouldReturnTrue_whenBookingIsCancelled() {
        // GIVEN
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);

        EventDetailDTO event = eventWithDate(LocalDate.now().plusDays(1));

        // WHEN
        boolean inactive = service.isInactive(booking, event);

        // THEN
        assertTrue(inactive);
    }

    @Test
    void isInactive_shouldReturnTrue_whenEventIsExpired() {
        // GIVEN
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);

        EventDetailDTO event = eventWithDate(LocalDate.now().minusDays(1));

        // WHEN
        boolean inactive = service.isInactive(booking, event);

        // THEN
        assertTrue(inactive);
    }

    @Test
    void isInactive_shouldReturnFalse_whenBookingIsActiveAndEventNotExpired() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);

        EventDetailDTO event = eventWithDate(LocalDate.now().plusDays(1));

        boolean inactive = service.isInactive(booking, event);

        assertFalse(inactive);
    }

    @Test
    void isCancelled_shouldReturnTrue_whenBookingIsCancelled() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);

        EventDetailDTO event = eventWithDate(LocalDate.now().plusDays(1));

        boolean cancelled = service.isCancelled(booking, event);

        assertTrue(cancelled);
    }

    @Test
    void isCancelled_shouldReturnFalse_whenBookingIsNotCancelled() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.PENDING);

        EventDetailDTO event = eventWithDate(LocalDate.now().plusDays(1));

        boolean cancelled = service.isCancelled(booking, event);

        assertFalse(cancelled);
    }


}
