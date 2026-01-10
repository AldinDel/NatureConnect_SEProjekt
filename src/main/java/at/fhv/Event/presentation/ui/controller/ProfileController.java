package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.user.CustomerProfileService;
import at.fhv.Event.domain.model.user.CustomerProfile;
import at.fhv.Event.domain.model.user.UserAccount;
import at.fhv.Event.presentation.ui.dto.ProfileForm;
import at.fhv.Event.presentation.ui.mapper.ProfileFormMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final CustomerProfileService customerProfileService;

    public ProfileController(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    @GetMapping
    public String profilePage(Model model) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        CustomerProfile profile =
                customerProfileService.getOrCreateProfileByEmail(email);

        ProfileForm form = ProfileFormMapper.toForm(profile);

        model.addAttribute("form", form);
        model.addAttribute("email", profile.getEmail());
        model.addAttribute("avatarUrl", profile.getAvatarUrl());

        return "profile/profile";
    }


    @PostMapping
    public String saveProfile(
            @ModelAttribute("form") ProfileForm form,
            RedirectAttributes redirectAttributes
    ) {
        String email = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        CustomerProfile profile =
                customerProfileService.getOrCreateProfileByEmail(email);

        ProfileFormMapper.applyToDomain(form, profile);
        customerProfileService.updateProfile(profile);

        redirectAttributes.addFlashAttribute("successMessage", "Profile saved successfully!");

        return "redirect:/profile";
    }

    @PostMapping("/avatar")
    public String uploadAvatar(@RequestParam("avatar") MultipartFile file) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerProfile profile =
                customerProfileService.getOrCreateProfileByEmail(email);

        if (!file.isEmpty()) {
            customerProfileService.updateAvatar(profile, file);
            customerProfileService.updateProfile(profile);
        }

        return "redirect:/profile";
    }

    @PostMapping("/avatar/remove")
    public String removeAvatar() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        CustomerProfile profile =
                customerProfileService.getOrCreateProfileByEmail(email);

        customerProfileService.removeAvatar(profile);
        customerProfileService.updateProfile(profile);

        return "redirect:/profile";
    }


}

