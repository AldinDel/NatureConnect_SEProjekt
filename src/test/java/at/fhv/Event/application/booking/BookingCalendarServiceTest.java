package at.fhv.Event.application.booking;

import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.junit.jupiter.api.Test;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// 1) resolveAllowedDays: leere Liste, wenn Event nicht recurring ist - keine erlaubten Tage
// 2) resolveAllowedDays: leere Liste, wenn recurring = true, aber keine recurrenceDays gesetzt sind
// 3) resolveAllowedDays: Enum-Namen werden zurückgegeben, wenn Event recurring ist und Tage gesetzt sind - recurrenceDays werden via Enum::name() gemappt

class BookingCalendarServiceTest {

    private final BookingCalendarService service = new BookingCalendarService();

    private EventDetailDTO eventWithRecurrence(boolean recurring, List<?> days) {
        return new EventDetailDTO(
                null,
                null,
                null,
                null,
                null,
                LocalDate.now(),
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
                recurring,
                null,
                null,
                (List) days
        );
    }

    @Test
    void resolveAllowedDays_shouldReturnEmptyList_whenEventIsNotRecurring() {
        EventDetailDTO event = eventWithRecurrence(false, List.of());

        List<String> result = service.resolveAllowedDays(event);

        assertTrue(result.isEmpty());
    }

    @Test
    void resolveAllowedDays_shouldReturnEmptyList_whenRecurrenceDaysIsNull() {
        EventDetailDTO event = eventWithRecurrence(true, null);

        List<String> result = service.resolveAllowedDays(event);

        assertTrue(result.isEmpty());
    }

    @Test
    void resolveAllowedDays_shouldReturnEnumNames_whenRecurringAndDaysProvided() {
        EventDetailDTO event = eventWithRecurrence(true, List.of(DayOfWeek.MONDAY, DayOfWeek.FRIDAY));

        List<String> result = service.resolveAllowedDays(event);

        assertEquals(List.of("MONDAY", "FRIDAY"), result);
    }

}
