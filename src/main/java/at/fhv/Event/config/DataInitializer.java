package at.fhv.Event.config;

import at.fhv.Event.infrastructure.persistence.user.RoleEntity;
import at.fhv.Event.infrastructure.persistence.user.RoleJpaRepository;
import at.fhv.Event.infrastructure.persistence.user.UserAccountEntity;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.OffsetDateTime;
import java.util.Set;

@Profile("!test")
@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initUsers(UserAccountJpaRepository userRepo, RoleJpaRepository roleRepo, PasswordEncoder encoder) {
        return args -> {
            // Rollen sicherstellen
            createRoleIfNotFound(roleRepo, "ADMIN");      // Backend
            createRoleIfNotFound(roleRepo, "FRONT");   // Frontend
            createRoleIfNotFound(roleRepo, "ORGANIZER");  // Organizer
            createRoleIfNotFound(roleRepo, "CUSTOMER");    // Customer

            // 1. Backend Mitarbeiter (Admin)
            createUserIfNotFound(userRepo, roleRepo, encoder, "aldin.delahmet@gmx.at", "aldin123", "Backend", "Mitarbeiter", "ADMIN");
            createUserIfNotFound(userRepo, roleRepo, encoder, "elif.akpnr28@gmail.com", "elif123", "Backend", "Mitarbeiter", "ADMIN");
            createUserIfNotFound(userRepo, roleRepo, encoder, "sumeyye.direk42@gmail.com", "sumi123", "Backend", "Mitarbeiter", "ADMIN");


            // 2. Frontend Mitarbeiter (Staff)
            createUserIfNotFound(userRepo, roleRepo, encoder, "aytennur.ozer@icloud.com", "ayti123", "Frontend", "Mitarbeiter", "FRONT");
            createUserIfNotFound(userRepo, roleRepo, encoder, "andrelichten03@gmail.com", "andre123", "Frontend", "Mitarbeiter", "FRONT");

            // 3. Organizer
            createUserIfNotFound(userRepo, roleRepo, encoder, "redbull@school.at", "org123", "RedBull", "Company", "ORGANIZER");

            // 4. Customer
            createUserIfNotFound(userRepo, roleRepo, encoder, "customer@school.at", "cust123", "Charlie", "Clarkson", "CUSTOMER");
        };
    }

    private void createRoleIfNotFound(RoleJpaRepository repo, String code) {
        if (repo.findByCode(code).isEmpty()) {
            RoleEntity role = new RoleEntity();
            role.setCode(code);
            repo.save(role);
        }
    }

    private void createUserIfNotFound(UserAccountJpaRepository userRepo, RoleJpaRepository roleRepo, PasswordEncoder encoder,
                                      String email, String rawPassword, String first, String last, String roleCode) {
        if (userRepo.findByEmailIgnoreCase(email).isEmpty()) {
            UserAccountEntity user = new UserAccountEntity();
            user.setEmail(email);
            user.setPasswordHash(encoder.encode(rawPassword));
            user.setFirstName(first);
            user.setLastName(last);
            user.setActive(true);
            user.setCreatedAt(OffsetDateTime.now());
            user.setUpdatedAt(OffsetDateTime.now());

            RoleEntity role = roleRepo.findByCode(roleCode).orElseThrow();
            user.setRoles(Set.of(role));

            userRepo.save(user);
            System.out.println("Created User: " + email + " (" + roleCode + ")");
        }
    }
}