package at.fhv.Event.rest.mapper;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class EquipmentResponseMapper {

    public EquipmentDTO toDTO(Equipment equipment) {
        return new EquipmentDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getUnitPrice(),
                equipment.isRentable(),
                false,
                equipment.getStock()

        );
    }
    public EquipmentDTO toDTO(Equipment equipment, boolean required) {
        return new EquipmentDTO(
                equipment.getId(),
                equipment.getName(),
                equipment.getUnitPrice(),
                equipment.isRentable(),
                required,
                equipment.getStock()
        );
    }
    public List<EquipmentDTO> toDTOList(List<Equipment> equipmentList) {
        List<EquipmentDTO> dtoList = new ArrayList<>();
        for (Equipment equipment : equipmentList) {
            EquipmentDTO dto = toDTO(equipment);
            dtoList.add(dto);
        }
        return dtoList;
    }
}
