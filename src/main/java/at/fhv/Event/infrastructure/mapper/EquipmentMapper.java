package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public Equipment toDomain(EquipmentEntity e) {
        if (e == null) {
            return null;
        }
        return new Equipment(
                e.getId(),
                e.getName(),
                e.getUnitPrice(),
                e.isRentable(),
                e.getStock()
        );
    }

    public EquipmentEntity toEntity(Equipment d) {
        if (d == null) return null;
        EquipmentEntity e = new EquipmentEntity();
        e.setId(d.getId());
        e.setName(d.getName());
        e.setUnitPrice(d.getUnitPrice());
        e.setRentable(d.isRentable());
        e.setStock(d.getStock());
        return e;
    }
}
