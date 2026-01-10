package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.audit.AuditLog;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Arrays;
import java.util.List;

@Controller
public class AuditLogController {

    private static final Logger logger = LoggerFactory.getLogger(AuditLogController.class);

    private final AuditLogService auditLogService;

    public AuditLogController(AuditLogService auditLogService) {
        this.auditLogService = auditLogService;
    }

    @GetMapping("/admin/logs")
    @PreAuthorize("hasRole('ADMIN')")
    public String logsOverview(
            @RequestParam(value = "actionType", required = false) String actionType,
            @RequestParam(value = "entityType", required = false) String entityType,
            Model model
    ) {
        logger.debug("Loading audit logs - actionType: {}, entityType: {}", actionType, entityType);

        List<AuditLog> logs;

        // Apply filters
        if (actionType != null && !actionType.isEmpty() && !actionType.equalsIgnoreCase("all")) {
            try {
                ActionType type = ActionType.valueOf(actionType);
                logs = auditLogService.getLogsByActionType(type);
                logger.debug("Filtered by action type: {} - Found {} logs", actionType, logs.size());
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid action type: {}", actionType);
                logs = auditLogService.getAllLogs();
            }
        } else if (entityType != null && !entityType.isEmpty() && !entityType.equalsIgnoreCase("all")) {
            logs = auditLogService.getLogsByEntityType(entityType);
            logger.debug("Filtered by entity type: {} - Found {} logs", entityType, logs.size());
        } else {
            logs = auditLogService.getAllLogs();
            logger.debug("Loading all logs - Found {} logs", logs.size());
        }

        // Add data to model
        model.addAttribute("logs", logs);
        model.addAttribute("actionTypes", Arrays.asList(ActionType.values()));
        model.addAttribute("selectedActionType", actionType != null ? actionType : "all");
        model.addAttribute("selectedEntityType", entityType != null ? entityType : "all");

        logger.info("Displaying {} audit logs", logs.size());

        return "users/audit_logs";
    }
}
