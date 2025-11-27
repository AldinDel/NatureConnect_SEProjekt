package at.fhv.Event.application.user;

import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

@Service
public class UserPermissionService {

    private final UserAccountJpaRepository userRepo;

    public UserPermissionService(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    public boolean canEdit(Authentication auth, EventDetailDTO event) {
        if (auth == null || !auth.isAuthenticated()) return false;

        // Admin und Frontend-Mitarbeiter (FRONT) dürfen immer editieren
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) return true;
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_FRONT"))) return true;

        // Organizer nur, wenn es ihr eigenes Event ist
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZER"))) {
            return userRepo.findByEmailIgnoreCase(auth.getName())
                    .map(u -> (u.getFirstName() + " " + u.getLastName()).equalsIgnoreCase(event.organizer()))
                    .orElse(false);
        }
        return false;
    }

    public boolean canCancel(Authentication auth, EventDetailDTO event) {
        if (auth == null || !auth.isAuthenticated()) return false;

        // Admin darf immer stornieren
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) return true;

        // WICHTIG: FRONT darf NICHT stornieren (laut deiner Anforderung)

        // Organizer nur, wenn es ihr eigenes Event ist
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZER"))) {
            return userRepo.findByEmailIgnoreCase(auth.getName())
                    .map(u -> (u.getFirstName() + " " + u.getLastName()).equalsIgnoreCase(event.organizer()))
                    .orElse(false);
        }
        return false;
    }
}