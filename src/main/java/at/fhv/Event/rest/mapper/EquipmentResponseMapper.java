package at.fhv.Event.rest.mapper;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Component;

@Component
public class EquipmentResponseMapper {

    public EquipmentDTO toDTO(Equipment eq, boolean required) {
        return new EquipmentDTO(
                eq.getId(),
                eq.getName(),
                eq.getUnitPrice(),
                eq.isRentable(),
                required,
                eq.getStock()

        );
    }
}
