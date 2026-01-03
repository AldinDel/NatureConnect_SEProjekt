package at.fhv.Event.application.user;

import at.fhv.Event.domain.model.user.UserAccountRepository;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

/**
 * Granular permission service for event operations.
 *
 * Role permissions:
 * - ADMIN: Can edit and cancel any event
 * - FRONT: Can edit any event, but CANNOT cancel events (frontend staff assistance role)
 * - ORGANIZER: Can edit and cancel only their own events
 * - CUSTOMER: No event management permissions (view and booking only)
 */
@Service
public class UserPermissionService {

    private final UserAccountRepository userRepo;

    public UserPermissionService(UserAccountRepository userRepo) {
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

        // FRONT (Frontend-Mitarbeiter) darf Events bearbeiten, aber NICHT stornieren
        // Dies verhindert, dass Frontend-Personal Events endgültig absagen kann

        // Organizer nur, wenn es ihr eigenes Event ist
        if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZER"))) {
            return userRepo.findByEmailIgnoreCase(auth.getName())
                    .map(u -> (u.getFirstName() + " " + u.getLastName()).equalsIgnoreCase(event.organizer()))
                    .orElse(false);
        }
        return false;
    }
}