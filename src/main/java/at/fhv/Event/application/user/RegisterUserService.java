package at.fhv.Event.application.user;

import at.fhv.Event.infrastructure.persistence.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class RegisterUserService {

    private final UserAccountJpaRepository userRepo;
    private final RoleJpaRepository roleRepo;
    private final CustomerProfileJpaRepository customerProfileRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(
            UserAccountJpaRepository userRepo,
            RoleJpaRepository roleRepo,
            CustomerProfileJpaRepository customerProfileRepo,
            PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerCustomer(String firstName, String lastName, String email, String password) {
        if (userRepo.findByEmailIgnoreCase(email).isPresent()) {
            throw new IllegalArgumentException("Diese E-Mail wird bereits verwendet.");
        }

        // 1. Login-Account erstellen
        UserAccountEntity user = new UserAccountEntity();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        // Rolle: Customer (FO_USER)
        RoleEntity role = roleRepo.findByCode("FO_USER")
                .orElseThrow(() -> new RuntimeException("Rolle FO_USER nicht gefunden."));
        user.setRoles(Set.of(role));

        UserAccountEntity savedUser = userRepo.save(user);

        // 2. Customer Profil erstellen (Wichtig für die Konsistenz)
        CustomerProfileEntity profile = new CustomerProfileEntity();
        profile.setUser(savedUser);
        profile.setFirstName(firstName);
        profile.setLastName(lastName);
        profile.setEmail(email);
        // Geburtstag bleibt NULL, da nicht im Formular
        profile.setCreatedAt(OffsetDateTime.now());
        profile.setUpdatedAt(OffsetDateTime.now());

        customerProfileRepo.save(profile);
    }
}