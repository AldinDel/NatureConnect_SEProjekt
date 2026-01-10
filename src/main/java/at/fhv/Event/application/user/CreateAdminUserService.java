package at.fhv.Event.application.user;

import at.fhv.Event.application.audit.AuditLogService;
import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.domain.model.audit.ActionType;
import at.fhv.Event.domain.model.exception.DuplicateEmailException;
import at.fhv.Event.domain.model.exception.InvalidPasswordException;
import at.fhv.Event.domain.model.exception.RoleNotFoundException;
import at.fhv.Event.domain.model.user.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class CreateAdminUserService {
    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final CustomerProfileRepository customerProfileRepo;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    public CreateAdminUserService(UserAccountRepository userRepo, RoleRepository roleRepo,
            CustomerProfileRepository customerProfileRepo, PasswordEncoder passwordEncoder,
            AuditLogService auditLogService) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public Long create(AdminUserEditRequest req) {
        String email = req.email().trim();

        userRepo.findByEmailIgnoreCase(email).ifPresent(u -> {
            throw new DuplicateEmailException(email);
        });

        String pw = req.password() == null ? "" : req.password().trim();
        if (pw.isEmpty()) {
            throw new InvalidPasswordException("Passwort is required..");
        }

        Role role = roleRepo.findByCode(req.role())
                .orElseThrow(() -> new RoleNotFoundException(req.role()));

        UserAccount user = new UserAccount();
        user.setFirstName(req.firstName().trim());
        user.setLastName(req.lastName().trim());
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(pw));
        user.setActive(req.active());
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());
        user.getRoles().clear();
        user.getRoles().add(role);

        UserAccount saved = userRepo.save(user);

        if ("CUSTOMER".equals(req.role())) {
            CustomerProfile profile = new CustomerProfile();
            profile.setUser(saved);
            profile.setFirstName(saved.getFirstName());
            profile.setLastName(saved.getLastName());
            profile.setEmail(saved.getEmail());
            profile.setCreatedAt(OffsetDateTime.now());
            profile.setUpdatedAt(OffsetDateTime.now());
            customerProfileRepo.save(profile);
        }

        // Audit log
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            String adminEmail = auth.getName();
            auditLogService.log(
                    adminEmail,
                    ActionType.CREATE,
                    "Created new user: " + saved.getFirstName() + " " + saved.getLastName(),
                    "UserAccount",
                    saved.getId(),
                    "Role: " + req.role() + ", Email: " + saved.getEmail()
            );
        }

        return saved.getId();
    }
}
