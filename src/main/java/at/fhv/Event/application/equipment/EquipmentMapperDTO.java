package at.fhv.Event.application.equipment;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapperDTO {

    public EquipmentDTO toDto(Equipment e, boolean required) {
        if (e == null) return null;
        return new EquipmentDTO(
                e.getId(),
                e.getName(),
                e.getUnitPrice(),
                e.isRentable(),
                required,
                e.getStock());
    }

    // Domain helpers
    public Equipment toDomainCreate(String name,
                                    java.math.BigDecimal price,
                                    boolean rentable,
                                    Integer stock) {
        return new Equipment(null, name, price, rentable, stock);
    }

    public Equipment toDomainUpdate(Long id,
                                    String name,
                                    java.math.BigDecimal price,
                                    boolean rentable,
                                    Integer stock) {
        return new Equipment(id, name, price, rentable, stock);
    }
}
