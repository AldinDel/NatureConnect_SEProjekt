package at.fhv.Event.infrastructure.persistence.event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventJpaRepository extends JpaRepository<EventEntity, Long> {

    @Query("SELECT e FROM EventEntity e LEFT JOIN FETCH e.eventEquipments ee LEFT JOIN FETCH ee.equipment WHERE e.id = :id")
    Optional<EventEntity> findByIdWithEquipments(@Param("id") Long id);

    @Query("SELECT e FROM EventEntity e WHERE e.date = :date")
    List<EventEntity> findByDate(@Param("date") LocalDate date);
}
