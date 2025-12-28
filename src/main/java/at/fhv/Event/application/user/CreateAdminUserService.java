package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.domain.model.user.*;
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

    public CreateAdminUserService(UserAccountRepository userRepo, RoleRepository roleRepo,
            CustomerProfileRepository customerProfileRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public Long create(AdminUserEditRequest req) {
        String email = req.email().trim();

        userRepo.findByEmailIgnoreCase(email).ifPresent(u -> {
            throw new IllegalArgumentException("Diese E-Mail wird bereits verwendet.");
        });

        String pw = req.password() == null ? "" : req.password().trim();
        if (pw.isEmpty()) {
            throw new IllegalArgumentException("Passwort ist erforderlich.");
        }

        Role role = roleRepo.findByCode(req.role())
                .orElseThrow(() -> new IllegalArgumentException("Rolle nicht gefunden: " + req.role()));

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
        return saved.getId();
    }
}
