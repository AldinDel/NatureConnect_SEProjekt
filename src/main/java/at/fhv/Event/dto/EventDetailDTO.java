package at.fhv.Event.dto;
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
        String difficulty,
        int minParticipants,
        int maxParticipants,
        java.math.BigDecimal price,
        String imageUrl,
        String audience,
        List<EquipmentDTO> equipment
) {public String duration() {
    if (startTime != null && endTime != null) {
        long hours = java.time.Duration.between(startTime, endTime).toHours();
        long minutes = java.time.Duration.between(startTime, endTime).toMinutesPart();
        if (minutes > 0) {
            return hours + "h " + minutes + "m";
        }
        return hours + " hours";
    }
    return "-";
}}
