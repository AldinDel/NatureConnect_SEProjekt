package at.fhv.Event.domain.model.equipment;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EquipmentRepository {
    Optional<Equipment> findById(Long id);
    List<Equipment> findAll();
    List<Equipment> findByRentableTrue();
    Map<Long, Equipment> findByIds(List<Long> ids);
    Equipment save(Equipment equipment);
    void updateStock(Long equipmentId, int newStock);
    Optional<Equipment> findByNameIgnoreCase(String name);
    void deleteById(Long id);

}
