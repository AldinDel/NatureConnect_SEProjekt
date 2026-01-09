package at.fhv.Event.application.audit;

import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.audit.AuditLog;
import at.fhv.Event.domain.model.audit.AuditLogRepository;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogService.class);

    private final AuditLogRepository auditLogRepository;
    private final UserAccountRepository userAccountRepository;

    public AuditLogService(
            AuditLogRepository auditLogRepository,
            UserAccountRepository userAccountRepository
    ) {
        this.auditLogRepository = auditLogRepository;
        this.userAccountRepository = userAccountRepository;
    }

    /**
     * Log an action with full details
     */
    public void log(
            String userEmail,
            ActionType actionType,
            String action,
            String entityType,
            Long entityId,
            String details,
            String ipAddress
    ) {
        try {
            UserAccount user = userAccountRepository.findByEmailIgnoreCase(userEmail)
                    .orElse(null);

            String username = user != null ? user.getFirstName() + " " + user.getLastName() : "Unknown";
            Long userId = user != null ? user.getId() : null;
            String userRole = user != null && !user.getRoles().isEmpty()
                    ? user.getRoles().iterator().next().getCode()
                    : "UNKNOWN";

            AuditLog log = new AuditLog(
                    userId,
                    username,
                    userEmail,
                    userRole,
                    action,
                    actionType,
                    entityType,
                    entityId,
                    details,
                    ipAddress
            );

            auditLogRepository.save(log);
            logger.debug("Audit log created: {} - {} - {}", userEmail, actionType, action);
        } catch (Exception e) {
            logger.error("Failed to create audit log: {}", e.getMessage(), e);
        }
    }

    /**
     * Log an action without IP address
     */
    public void log(
            String userEmail,
            ActionType actionType,
            String action,
            String entityType,
            Long entityId,
            String details
    ) {
        log(userEmail, actionType, action, entityType, entityId, details, null);
    }

    /**
     * Log a simple action without details
     */
    public void log(
            String userEmail,
            ActionType actionType,
            String action,
            String entityType,
            Long entityId
    ) {
        log(userEmail, actionType, action, entityType, entityId, null, null);
    }

    /**
     * Get all logs ordered by timestamp (newest first)
     */
    public List<AuditLog> getAllLogs() {
        return auditLogRepository.findAllOrderByTimestampDesc();
    }

    /**
     * Get logs for a specific user
     */
    public List<AuditLog> getLogsByUserId(Long userId) {
        return auditLogRepository.findByUserId(userId);
    }

    /**
     * Get logs by action type
     */
    public List<AuditLog> getLogsByActionType(ActionType actionType) {
        return auditLogRepository.findByActionType(actionType);
    }

    /**
     * Get logs by entity type
     */
    public List<AuditLog> getLogsByEntityType(String entityType) {
        return auditLogRepository.findByEntityType(entityType);
    }
}
