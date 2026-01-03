package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.user.CustomerProfileService;
import at.fhv.Event.domain.model.user.CustomerProfile;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAdvice {

    private final CustomerProfileService customerProfileService;

    public GlobalControllerAdvice(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @ModelAttribute
    public void addNavbarUser(Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return;
        }

        CustomerProfile profile =
                customerProfileService.getOrCreateProfileByEmail(auth.getName());

        String firstName = capitalize(profile.getFirstName());
        String lastName  = capitalize(profile.getLastName());

        model.addAttribute("currentUserName", firstName + " " + lastName);
        model.addAttribute("avatarUrl", profile.getAvatarUrl());
        model.addAttribute(
                "userInitials",
                (firstName.substring(0, 1) + lastName.substring(0, 1)).toUpperCase()
        );
    }

    private String capitalize(String value) {
        if (value == null || value.isBlank()) {
            return "";
        }
        return value.substring(0, 1).toUpperCase()
                + value.substring(1).toLowerCase();
    }
}
