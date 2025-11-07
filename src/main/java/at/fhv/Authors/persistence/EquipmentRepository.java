package at.fhv.Authors.persistence;

import at.fhv.Authors.domain.model.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    Optional<Equipment> findByNameIgnoreCase(String name);
}
