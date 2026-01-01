package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.request.user.LoginRequest;
import at.fhv.Event.application.request.user.RegisterUserRequest;
import at.fhv.Event.application.user.AuthValidator;
import at.fhv.Event.application.user.CustomUserDetailsService;
import at.fhv.Event.application.user.RegisterUserService;
import at.fhv.Event.domain.model.exception.DuplicateEmailException;
import at.fhv.Event.domain.model.exception.ValidationError;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class AuthController {
    private final RegisterUserService registerService;
    private final CustomUserDetailsService userDetailsService;
    private final AuthValidator authValidator;
    private final ErrorMessageService errorMessageService;
    private final ObjectMapper objectMapper;

    public AuthController(RegisterUserService registerService, CustomUserDetailsService userDetailsService, AuthValidator authValidator,
                          ErrorMessageService errorMessageService, ObjectMapper objectMapper) {
        this.registerService = registerService;
        this.userDetailsService = userDetailsService;
        this.authValidator = authValidator;
        this.errorMessageService = errorMessageService;
        this.objectMapper = objectMapper;
    }

    @GetMapping("/login")
    public String showLoginPage(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "logout", required = false) String logout,
            Model model) {

        if (!model.containsAttribute("loginRequest")) {
            model.addAttribute("loginRequest", new LoginRequest());
        }

        if (error != null) {
            model.addAttribute("error", "Invalid email or password.");
        }

        if (logout != null) {
            model.addAttribute("success", "Successfully logged out.");
        }

        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        if (!model.containsAttribute("registerRequest")) {
            model.addAttribute("registerRequest", new RegisterUserRequest());
        }
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("registerRequest") RegisterUserRequest request,
                               BindingResult bindingResult,
                               @RequestParam(required = false) String redirect,
                               HttpServletRequest httpRequest,
                               HttpServletResponse httpResponse,
                               RedirectAttributes redirectAttributes,
                               Model model) {
        List<ValidationError> customErrors = authValidator.validateRegister(request);
        Map<String, String> fieldErrors = new HashMap<>();
        if (bindingResult.hasErrors()) {
            bindingResult.getFieldErrors().forEach(error ->
                    fieldErrors.put(error.getField(), error.getDefaultMessage())
            );
        }

        customErrors.forEach(error ->
                fieldErrors.put(error.get_field(), errorMessageService.getMessage(error.get_message()))
        );

        if (!fieldErrors.isEmpty()) {
            try {
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("fieldErrorsJson", objectMapper.writeValueAsString(fieldErrors));
            } catch (JsonProcessingException e) {
                model.addAttribute("fieldErrors", fieldErrors);
            }
            model.addAttribute("registerRequest", request);
            return "auth/register";
        }

        try {
            registerService.registerCustomer(
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    request.getPassword()
            );

            var userDetails = userDetailsService.loadUserByUsername(request.getEmail());


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

        } catch (DuplicateEmailException e) {
            fieldErrors.put("email", "This email is already registered");
            try {
                model.addAttribute("fieldErrors", fieldErrors);
                model.addAttribute("fieldErrorsJson", objectMapper.writeValueAsString(fieldErrors));
            } catch (JsonProcessingException ex) {
                model.addAttribute("fieldErrors", fieldErrors);
            }
            model.addAttribute("registerRequest", request);
            return "auth/register";

        } catch (Exception e) {
            model.addAttribute("error", "Registration failed: " + e.getMessage());
            model.addAttribute("registerRequest", request);
            return "auth/register";
        }
    }
}