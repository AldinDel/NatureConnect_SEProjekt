package at.fhv.Event.application.user;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.exception.DuplicateEmailException;
import at.fhv.Event.domain.model.exception.RoleNotFoundException;
import at.fhv.Event.domain.model.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateAdminUserService {
    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final CustomerProfileRepository customerProfileRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public UpdateAdminUserService(UserAccountRepository userRepo, RoleRepository roleRepo,
            CustomerProfileRepository customerProfileRepo, PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public void update(Long id, AdminUserEditRequest req) {
        UserAccount user = userRepo.findById(id).orElseThrow();

        String newEmail = req.email().trim();
        String oldEmail = user.getEmail();

        // Store old values for audit log
        String oldRole = user.getRoles().isEmpty() ? "NONE" : user.getRoles().iterator().next().getCode();
        boolean wasActive = Boolean.TRUE.equals(user.getActive());

        userRepo.findByEmailIgnoreCase(newEmail).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new DuplicateEmailException(newEmail);
            }
        });

        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setEmail(newEmail);
        user.setActive(req.active());
        user.setUpdatedAt(OffsetDateTime.now());

        String pw = req.password() == null ? "" : req.password().trim();
        if (!pw.isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(pw));
        }

        Role role = roleRepo.findByCode(req.role())
                .orElseThrow(() -> new RoleNotFoundException(req.role()));
        user.getRoles().clear();
        user.getRoles().add(role);

        UserAccount saved = userRepo.save(user);

        // Audit log for role change
        if (!oldRole.equals(req.role())) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String adminEmail = auth.getName();
                auditLogService.log(
                        adminEmail,
                        ActionType.ROLE_CHANGE,
                        "Changed role for user: " + saved.getFirstName() + " " + saved.getLastName(),
                        "UserAccount",
                        saved.getId(),
                        "Role changed from " + oldRole + " to " + req.role()
                );
            }
        }

        // Audit log for activate/deactivate (only if status changed)
        if (wasActive != req.active()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated()) {
                String adminEmail = auth.getName();
                if (req.active()) {
                    auditLogService.log(
                            adminEmail,
                            ActionType.ACTIVATE,
                            "Activated user: " + saved.getFirstName() + " " + saved.getLastName(),
                            "UserAccount",
                            saved.getId()
                    );
                } else {
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

        if ("CUSTOMER".equals(req.role())) {
            CustomerProfile profile = customerProfileRepo.findByUserId(saved.getId())
                    .orElseGet(CustomerProfile::new);

            profile.setUser(saved);
            profile.setFirstName(saved.getFirstName());
            profile.setLastName(saved.getLastName());
            profile.setEmail(saved.getEmail());
            profile.setUpdatedAt(OffsetDateTime.now());

            customerProfileRepo.save(profile);
        } else if (!oldEmail.equalsIgnoreCase(newEmail)) {
            customerProfileRepo.findByUserId(saved.getId()).ifPresent(p -> {
                p.setEmail(saved.getEmail());
                p.setUpdatedAt(OffsetDateTime.now());
                customerProfileRepo.save(p);
            });
        }
    }
}
