package at.fhv.Event.domain.model.audit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditLogRepository {

    AuditLog save(AuditLog auditLog);

    Optional<AuditLog> findById(Long id);

    List<AuditLog> findAll();

    List<AuditLog> findByUserId(Long userId);

    List<AuditLog> findByActionType(ActionType actionType);

    List<AuditLog> findByEntityType(String entityType);

    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);

    List<AuditLog> findAllOrderByTimestampDesc();
}
