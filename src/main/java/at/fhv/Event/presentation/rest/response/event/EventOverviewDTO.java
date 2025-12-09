package at.fhv.Event.presentation.rest.response.event;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public record EventOverviewDTO(
        Long id,
        String title,
        String description,
        String displayOrganizer,
        String organizer,
        String category,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        String difficulty,
        Integer minParticipants,
        Integer maxParticipants,
        BigDecimal price,
        String imageUrl,
        String audience,
        Boolean cancelled
) {}
