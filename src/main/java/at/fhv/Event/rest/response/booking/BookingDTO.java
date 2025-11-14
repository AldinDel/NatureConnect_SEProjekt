package at.fhv.Event.rest.response.booking;

import java.time.LocalDate;

public record BookingDTO(
        Long id,
        Long eventId,
        String customerName,
        int numberOfParticipants,
        LocalDate date
) {}
