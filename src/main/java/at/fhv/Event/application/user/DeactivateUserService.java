package at.fhv.Event.application.user;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.domain.model.user.UserAccountRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DeactivateUserService {

    private final UserAccountRepository userRepo;
    private final AuditLogService auditLogService;

    public DeactivateUserService(UserAccountRepository userRepo, AuditLogService auditLogService) {
        this.userRepo = userRepo;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void deactivate(Long userId) {
        UserAccount user = userRepo.findById(userId).orElseThrow();
        user.setActive(false);
        UserAccount saved = userRepo.save(user);

        // Audit log
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String adminEmail = auth.getName();
            auditLogService.log(
                    adminEmail,
                    ActionType.DEACTIVATE,
                    "Deactivated user: " + saved.getFirstName() + " " + saved.getLastName(),
                    "UserAccount",
                    saved.getId()
            );
        }
    }
}
