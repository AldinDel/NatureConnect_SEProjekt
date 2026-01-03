package at.fhv.Event.presentation.rest.response.event;

import at.fhv.Event.domain.model.event.Difficulty;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;

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
        List<String> hikeRouteKeys,
        String audience

) {}