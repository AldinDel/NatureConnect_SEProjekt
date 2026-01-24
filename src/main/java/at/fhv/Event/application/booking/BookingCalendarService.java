package at.fhv.Event.application.booking;

import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookingCalendarService {

    public List<String> resolveAllowedDays(EventDetailDTO event) {
        if (!event.recurring() || event.recurrenceDays() == null) {
            return List.of();
        }

        return event.recurrenceDays()
                .stream()
                .map(Enum::name)
                .toList();
    }
}
