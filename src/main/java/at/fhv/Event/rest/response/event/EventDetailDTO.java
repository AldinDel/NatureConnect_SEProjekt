package at.fhv.Event.rest.response.event;

import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public record EventDetailDTO(
        Long id,
        String title,
        String description,
        String organizer,
        String category,
        LocalDate date,
        LocalTime startTime,
        LocalTime endTime,
        String location,
        Difficulty difficulty,
        Integer minParticipants,
        Integer maxParticipants,
        BigDecimal price,
        String imageUrl,
        Boolean cancelled,
        List<EquipmentDTO> equipments,
        List<Long> requiredEquipmentIds,
        List<Long> optionalEquipmentIds,
        String audience
) {
    public String duration() {
        if (startTime == null || endTime == null) {
            return "N/A";
        }

        long hours = java.time.Duration.between(startTime, endTime).toHours();
        long minutes = java.time.Duration.between(startTime, endTime).toMinutes() % 60;

        if (hours > 0 && minutes > 0) {
            return hours + " hours " + minutes + " minutes";
        } else if (hours > 0) {
            return hours + " hour" + (hours > 1 ? "s" : "");
        } else {
            return minutes + " minutes";
        }
    }
}