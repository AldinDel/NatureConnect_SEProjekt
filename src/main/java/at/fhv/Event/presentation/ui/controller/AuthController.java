package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.request.user.RegisterUserRequest;
import at.fhv.Event.application.user.CustomUserDetailsService;
import at.fhv.Event.application.user.RegisterUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final RegisterUserService registerService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(RegisterUserService registerService,CustomUserDetailsService userDetailsService) {
        this.registerService = registerService;
        this.userDetailsService = userDetailsService;
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
    public String registerUser(@ModelAttribute RegisterUserRequest request,
                               @RequestParam(required = false) String redirect,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpResponse,
                               RedirectAttributes redirectAttributes) {
        try {
            registerService.registerCustomer(
                    request.firstName(),
                    request.lastName(),
                    request.email(),
                    request.password()
            );

            var userDetails = userDetailsService.loadUserByUsername(request.email());


            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(authToken);

            httpRequest.getSession(true)
                    .setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());


            if (redirect != null && !redirect.isBlank()) {
                return "redirect:" + redirect;
            }

            return "redirect:/";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/register?redirect=" + (redirect != null ? redirect : "");
        }
    }
}