package at.fhv.Event.infrastructure.persistence.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {
    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.eventEquipments ee LEFT JOIN FETCH ee.equipment WHERE e.id = :id")
    Optional<EventEntity> findByIdWithEquipments(Long id);
}
