package at.fhv.Event.persistence;

import at.fhv.Event.domain.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByNameIgnoreCase(String name);
    Optional<Equipment> findByName(String name);
}
