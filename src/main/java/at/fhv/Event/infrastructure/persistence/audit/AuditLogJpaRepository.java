package at.fhv.Event.infrastructure.persistence.audit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface AuditLogJpaRepository extends JpaRepository<AuditLogJpaEntity, Long> {

    List<AuditLogJpaEntity> findByUserId(Long userId);

    List<AuditLogJpaEntity> findByActionType(String actionType);

    List<AuditLogJpaEntity> findByEntityType(String entityType);

    List<AuditLogJpaEntity> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    @Query("SELECT a FROM AuditLogJpaEntity a ORDER BY a.timestamp DESC")
    List<AuditLogJpaEntity> findAllOrderByTimestampDesc();

    @Query("SELECT a FROM AuditLogJpaEntity a WHERE a.userId = :userId ORDER BY a.timestamp DESC")
    List<AuditLogJpaEntity> findByUserIdOrderByTimestampDesc(@Param("userId") Long userId);
}
