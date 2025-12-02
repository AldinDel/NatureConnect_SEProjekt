package at.fhv.Event.presentation.rest.response.event;

import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;

import java.math.BigDecimal;
import java.util.List;

public record EventDTO(
        Long id,
        String title,
        String description,
        String location,
        BigDecimal price,
        boolean cancelled,
        List<EquipmentDTO> equipments
) {}
