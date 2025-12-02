package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.event.SearchEventService;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository; // Importieren
import org.springframework.security.core.Authentication; // Importieren
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final SearchEventService searchService;
    private final UserAccountJpaRepository userRepo; // Neu

    // Constructor Injection
    public HomeController(SearchEventService searchService, UserAccountJpaRepository userRepo) {
        this.searchService = searchService;
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String home(Model model, Authentication auth) {
        // Events laden
        model.addAttribute("events", searchService.getAll());

        // User Name für Navbar laden, falls eingeloggt
        if (auth != null && auth.isAuthenticated()) {
            userRepo.findByEmailIgnoreCase(auth.getName()).ifPresent(u ->
                    model.addAttribute("currentUserName", u.getFirstName() + " " + u.getLastName())
            );
        }

        return "nature_connect";
    }
}