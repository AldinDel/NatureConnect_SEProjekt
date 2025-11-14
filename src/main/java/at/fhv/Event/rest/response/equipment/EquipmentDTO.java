package at.fhv.Event.rest.response.equipment;

import java.math.BigDecimal;

public record EquipmentDTO(
        Long id,
        String name,
        BigDecimal unitPrice,
        boolean rentable,
        boolean required
) {}
