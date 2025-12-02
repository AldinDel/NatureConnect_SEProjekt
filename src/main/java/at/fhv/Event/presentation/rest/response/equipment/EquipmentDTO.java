package at.fhv.Event.presentation.rest.response.equipment;

import java.math.BigDecimal;

public record EquipmentDTO(
        Long id,
        String name,
        BigDecimal unitPrice,
        boolean rentable,
        boolean required,
        Integer stock
) {}
