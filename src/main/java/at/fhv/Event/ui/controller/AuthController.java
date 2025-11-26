package at.fhv.Event.ui.controller;

import at.fhv.Event.application.user.RegisterUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final RegisterUserService registerService;

    public AuthController(RegisterUserService registerService) {
        this.registerService = registerService;
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (error != null) {
            model.addAttribute("error", "Ungültige E-Mail oder Passwort.");
        }

        if (logout != null) {
            model.addAttribute("success", "Erfolgreich abgemeldet.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            Model model) {

        try {
            registerService.registerCustomer(firstName, lastName, email, password);
            return "redirect:/login?success=Registrierung erfolgreich. Bitte einloggen.";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "auth/register";
        }
    }
}