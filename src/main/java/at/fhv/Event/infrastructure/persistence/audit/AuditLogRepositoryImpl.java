package at.fhv.Event.infrastructure.persistence.audit;

import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.audit.AuditLog;
import at.fhv.Event.domain.model.audit.AuditLogRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class AuditLogRepositoryImpl implements AuditLogRepository {

    private final AuditLogJpaRepository jpa;

    public AuditLogRepositoryImpl(AuditLogJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public AuditLog save(AuditLog auditLog) {
        AuditLogJpaEntity entity = toEntity(auditLog);
        AuditLogJpaEntity saved = jpa.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<AuditLog> findById(Long id) {
        return jpa.findById(id).map(this::toDomain);
    }

    @Override
    public List<AuditLog> findAll() {
        return jpa.findAll().stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByUserId(Long userId) {
        return jpa.findByUserId(userId).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByActionType(ActionType actionType) {
        return jpa.findByActionType(actionType.name()).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByEntityType(String entityType) {
        return jpa.findByEntityType(entityType).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end) {
        return jpa.findByTimestampBetween(start, end).stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AuditLog> findAllOrderByTimestampDesc() {
        return jpa.findAllOrderByTimestampDesc().stream()
                .map(this::toDomain)
                .toList();
    }

    private AuditLogJpaEntity toEntity(AuditLog domain) {
        AuditLogJpaEntity entity = new AuditLogJpaEntity();
        entity.setUserId(domain.getUserId());
        entity.setUsername(domain.getUsername());
        entity.setUserEmail(domain.getUserEmail());
        entity.setUserRole(domain.getUserRole());
        entity.setAction(domain.getAction());
        entity.setActionType(domain.getActionType() != null ? domain.getActionType().name() : null);
        entity.setEntityType(domain.getEntityType());
        entity.setEntityId(domain.getEntityId());
        entity.setDetails(domain.getDetails());
        entity.setTimestamp(domain.getTimestamp());
        entity.setIpAddress(domain.getIpAddress());
        return entity;
    }

    private AuditLog toDomain(AuditLogJpaEntity entity) {
        AuditLog domain = new AuditLog(
                entity.getUserId(),
                entity.getUsername(),
                entity.getUserEmail(),
                entity.getUserRole(),
                entity.getAction(),
                entity.getActionType() != null ? ActionType.valueOf(entity.getActionType()) : null,
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getDetails(),
                entity.getIpAddress()
        );
        domain.setId(entity.getId());
        domain.setTimestamp(entity.getTimestamp());
        return domain;
    }
}
