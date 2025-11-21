package at.fhv.Event.infrastructure.persistence.equipment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EquipmentJpaRepository extends JpaRepository<EquipmentEntity, Long> {
    Optional<EquipmentEntity> findByNameIgnoreCase(String name);
    List<EquipmentEntity> findByRentableTrue();

}
