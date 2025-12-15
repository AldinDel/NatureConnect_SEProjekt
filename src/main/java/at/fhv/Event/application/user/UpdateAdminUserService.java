package at.fhv.Event.application.user;

import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.infrastructure.persistence.user.CustomerProfileEntity;
import at.fhv.Event.infrastructure.persistence.user.CustomerProfileJpaRepository;
import at.fhv.Event.infrastructure.persistence.user.RoleEntity;
import at.fhv.Event.infrastructure.persistence.user.RoleJpaRepository;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class UpdateAdminUserService {

    private final UserAccountJpaRepository userRepo;
    private final RoleJpaRepository roleRepo;
    private final CustomerProfileJpaRepository customerProfileRepo;
    private final PasswordEncoder passwordEncoder;

    public UpdateAdminUserService(
            UserAccountJpaRepository userRepo,
            RoleJpaRepository roleRepo,
            CustomerProfileJpaRepository customerProfileRepo,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void update(Long id, AdminUserEditRequest req) {
        UserAccountEntity user = userRepo.findById(id).orElseThrow();

        String newEmail = req.email().trim();
        String oldEmail = user.getEmail();

        userRepo.findByEmailIgnoreCase(newEmail).ifPresent(existing -> {
            if (!existing.getId().equals(id)) {
                throw new IllegalArgumentException("Diese E-Mail wird bereits verwendet.");
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

        RoleEntity role = roleRepo.findByCode(req.role())
                .orElseThrow(() -> new IllegalArgumentException("Rolle nicht gefunden: " + req.role()));
        user.getRoles().clear();
        user.getRoles().add(role);

        UserAccountEntity saved = userRepo.save(user);

        if ("CUSTOMER".equals(req.role())) {
            CustomerProfileEntity profile = customerProfileRepo.findByUser_Id(saved.getId())
                    .orElseGet(CustomerProfileEntity::new);

            profile.setUser(saved);
            profile.setFirstName(saved.getFirstName());
            profile.setLastName(saved.getLastName());
            profile.setEmail(saved.getEmail());
            profile.setUpdatedAt(OffsetDateTime.now());

            customerProfileRepo.save(profile);
        } else if (!oldEmail.equalsIgnoreCase(newEmail)) {
            customerProfileRepo.findByUser_Id(saved.getId()).ifPresent(p -> {
                p.setEmail(saved.getEmail());
                p.setUpdatedAt(OffsetDateTime.now());
                customerProfileRepo.save(p);
            });
        }
    }
}
