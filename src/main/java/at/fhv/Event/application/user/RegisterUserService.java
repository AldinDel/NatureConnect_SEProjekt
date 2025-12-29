package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.exception.DuplicateEmailException;
import at.fhv.Event.domain.model.exception.RoleNotFoundException;
import at.fhv.Event.domain.model.user.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Set;

@Service
public class RegisterUserService {
    private final UserAccountRepository userRepo;
    private final RoleRepository roleRepo;
    private final CustomerProfileRepository customerProfileRepo;
    private final PasswordEncoder passwordEncoder;

    public RegisterUserService(UserAccountRepository userRepo, RoleRepository roleRepo,
            CustomerProfileRepository customerProfileRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.roleRepo = roleRepo;
        this.customerProfileRepo = customerProfileRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void registerCustomer(String firstName, String lastName, String email, String password) {
        if (userRepo.findByEmailIgnoreCase(email).isPresent()) {
            throw new DuplicateEmailException(email);
        }

        // 1. Login-Account erstellen
        UserAccount user = new UserAccount();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password));
        user.setActive(true);
        user.setCreatedAt(OffsetDateTime.now());
        user.setUpdatedAt(OffsetDateTime.now());

        // Rolle: Customer
        Role role = roleRepo.findByCode("CUSTOMER")
                .orElseThrow(() -> new RoleNotFoundException("CUSTOMER"));
        user.setRoles(Set.of(role));

        UserAccount savedUser = userRepo.save(user);

        // 2. Customer Profil erstellen (Wichtig für die Konsistenz)
        CustomerProfile profile = new CustomerProfile();
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