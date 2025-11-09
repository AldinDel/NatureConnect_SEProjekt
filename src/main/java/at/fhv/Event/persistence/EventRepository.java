package at.fhv.Event.persistence;

import at.fhv.Event.domain.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByCategoryIgnoreCase(String category);

    List<Event> findByTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String title, String description);

    List<Event> findByCategoryIgnoreCaseAndTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
            String category, String title, String description);

    /** Lägt ein Event anhand seiner ID und holt dabei gleich alle zugehörigen Equipments mit
     * Verhindert unnötige zusätzliche Datenbnakabfragen
     * */
    @Query("""
        SELECT e FROM Event e
        LEFT JOIN FETCH e.eventEquipments eq
        LEFT JOIN FETCH eq.equipment
        WHERE e.id = :id
    """)
    Optional<Event> findByIdWithEquipments(@Param("id") Long id);
}
