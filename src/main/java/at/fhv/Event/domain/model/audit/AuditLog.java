package at.fhv.Event.domain.model.audit;

import java.time.LocalDateTime;

public class AuditLog {

    private Long id;
    private Long userId;
    private String username;
    private String userEmail;
    private String userRole;
    private String action;
    private ActionType actionType;
    private String entityType;
    private Long entityId;
    private String details;
    private LocalDateTime timestamp;
    private String ipAddress;

    public AuditLog() {
        this.timestamp = LocalDateTime.now();
    }

    public AuditLog(
            Long userId,
            String username,
            String userEmail,
            String userRole,
            String action,
            ActionType actionType,
            String entityType,
            Long entityId,
            String details,
            String ipAddress
    ) {
        this.userId = userId;
        this.username = username;
        this.userEmail = userEmail;
        this.userRole = userRole;
        this.action = action;
        this.actionType = actionType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.details = details;
        this.timestamp = LocalDateTime.now();
        this.ipAddress = ipAddress;
    }

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getEntityType() {
        return entityType;
    }

    public void setEntityType(String entityType) {
        this.entityType = entityType;
    }

    public Long getEntityId() {
        return entityId;
    }

    public void setEntityId(Long entityId) {
        this.entityId = entityId;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }
}
