package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final UserAccountJpaRepository userRepo;

    public GlobalControllerAdvice(UserAccountJpaRepository userRepo) {
        this.userRepo = userRepo;
    }

    @ModelAttribute
    public void addGlobalAttributes(Model model, Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            // Lädt den Namen für die Navbar auf JEDER Seite
            userRepo.findByEmailIgnoreCase(auth.getName()).ifPresent(u -> {
                model.addAttribute("currentUserName", u.getFirstName() + " " + u.getLastName());
            });
        }
    }
}