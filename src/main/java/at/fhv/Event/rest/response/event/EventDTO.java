package at.fhv.Event.rest.response.event;

import java.math.BigDecimal;
import java.util.List;

public record EventDTO(
        Long id,
        String title,
        String description,
        String location,
        BigDecimal price,
        boolean isCancelled,
        List<at.fhv.Event.rest.response.equipment.EquipmentDTO> equipments
) {}
