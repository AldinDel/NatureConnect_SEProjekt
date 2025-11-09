package at.fhv.Event.dto;

import java.math.BigDecimal;

public record EquipmentDTO(
        Long id,
        String name,
        BigDecimal price,
        boolean rentable,
        boolean required
) {}
