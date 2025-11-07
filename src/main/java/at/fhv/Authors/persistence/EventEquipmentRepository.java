package at.fhv.Authors.persistence;

import at.fhv.Authors.domain.model.EventEquipment;
import at.fhv.Authors.domain.model.EventEquipmentId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventEquipmentRepository extends JpaRepository<EventEquipment, EventEquipmentId> {}
