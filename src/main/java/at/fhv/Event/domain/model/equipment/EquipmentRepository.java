package at.fhv.Event.domain.model.equipment;

import java.util.List;
import java.util.Optional;

public interface EquipmentRepository {
    Optional<Equipment> findById(Long id);
    Optional<Equipment> findByNameIgnoreCase(String name);
    List<Equipment> findAll();
    void save(Equipment equipment);
    void deleteById(Long id);
}
