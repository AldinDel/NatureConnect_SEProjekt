package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.request.user.AdminUserEditDTO;
import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.application.user.*;
import at.fhv.Event.domain.model.exception.DuplicateEmailException;
import at.fhv.Event.domain.model.exception.InvalidPasswordException;
import at.fhv.Event.domain.model.exception.RoleNotFoundException;
import at.fhv.Event.domain.model.exception.UserNotFoundException;
import at.fhv.Event.infrastructure.persistence.user.RoleJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AdminUserController {
    private static final Logger log = LoggerFactory.getLogger(AdminUserController.class);

    private final GetAdminUsersService getAdminUsersService;
    private final DeactivateUserService deactivateUserService;
    private final GetAdminUserForEditService getAdminUserForEditService;
    private final UpdateAdminUserService updateAdminUserService;
    private final RoleJpaRepository roleRepo;
    private final CreateAdminUserService createAdminUserService;
    private final ErrorMessageService errorMessageService;

    public AdminUserController(
            GetAdminUsersService getAdminUsersService,
            DeactivateUserService deactivateUserService,
            GetAdminUserForEditService getAdminUserForEditService,
            UpdateAdminUserService updateAdminUserService,
            CreateAdminUserService createAdminUserService,
            RoleJpaRepository roleRepo,
            ErrorMessageService errorMessageService
    ) {
        this.getAdminUsersService = getAdminUsersService;
        this.deactivateUserService = deactivateUserService;
        this.getAdminUserForEditService = getAdminUserForEditService;
        this.updateAdminUserService = updateAdminUserService;
        this.createAdminUserService = createAdminUserService;
        this.roleRepo = roleRepo;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String usersOverview(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            Model model, RedirectAttributes redirectAttributes) {
        try {
            String roleClean = (role == null || role.trim().isEmpty() || role.equalsIgnoreCase("all") || role.equalsIgnoreCase("all roles")) ? "" : role.trim();

            boolean hasQuery = q != null && !q.trim().isEmpty();

            if (!hasQuery && roleClean.isEmpty() && (role == null || role.trim().isEmpty())) {
                model.addAttribute("users", getAdminUsersService.getLatestUsers(5));
            } else {
                model.addAttribute("users", getAdminUsersService.search(q, roleClean, 50));
            }

            model.addAttribute("q", q == null ? "" : q);
            model.addAttribute("role", roleClean.isEmpty() ? "all" : roleClean);

            return "users/users-admin-overview";
        } catch (Exception e) {
            log.error("Failed to load users overview", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/";
        }
    }

    @PostMapping("/admin/users/{id}/deactivate")
    @PreAuthorize("hasRole('ADMIN')")
    public String deactivateUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            RedirectAttributes redirectAttributes
    ) {
        try {
            deactivateUserService.deactivate(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deactivated successfully.");
        } catch (Exception e) {
            log.error("Failed to deactivate user: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("errorMessage", message);
        }


        String qPart = (q == null || q.isBlank()) ? "" : "q=" + q.trim();
        String rolePart = (role == null || role.isBlank()) ? "" : "role=" + role.trim();

        if (!qPart.isEmpty() && !rolePart.isEmpty()) return "redirect:/admin/users?" + qPart + "&" + rolePart;
        if (!qPart.isEmpty()) return "redirect:/admin/users?" + qPart;
        if (!rolePart.isEmpty()) return "redirect:/admin/users?" + rolePart;
        return "redirect:/admin/users";
    }

    @GetMapping("/admin/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String editUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            Model model, RedirectAttributes redirectAttributes
    ) {
        try {
            AdminUserEditDTO user = getAdminUserForEditService.getById(id);

            model.addAttribute("user", user);
            model.addAttribute("roles", roleRepo.findAll().stream().map(r -> r.getCode()).sorted().toList());
            model.addAttribute("q", q == null ? "" : q);
            model.addAttribute("roleFilter", role == null ? "all" : role);

            return "users/users-admin-edit";
        } catch (UserNotFoundException e) {
            log.error("User not found for editing: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getUserId());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return buildRedirectUrl(q, role);

        } catch (Exception e) {
            log.error("Failed to load user for editing: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return buildRedirectUrl(q, role);
        }
    }

    @PostMapping("/admin/users/{id}/edit")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateUser(
            @PathVariable("id") Long id,
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam String email,
            @RequestParam String roleCode,
            @RequestParam(required = false, defaultValue = "false") boolean active,
            @RequestParam(required = false) String password,
            RedirectAttributes redirectAttributes
    ) {
        try {
            updateAdminUserService.update(id, new at.fhv.Event.application.request.user.AdminUserEditRequest(
                    firstName, lastName, email, roleCode, active, password
            ));
            redirectAttributes.addFlashAttribute("successMessage", "User updated successfully.");
        } catch (UserNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    errorMessageService.getMessage(e.getErrorCode(), e.getUserId())
            );
            return buildRedirectUrl(q, role);

        } catch (RoleNotFoundException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    errorMessageService.getMessage(e.getErrorCode(), e.getRoleCode()));
            return "redirect:/admin/users/" + id + "/edit";

        } catch (InvalidPasswordException e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    errorMessageService.getMessage(e.getErrorCode()));
            return "redirect:/admin/users/" + id + "/edit";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "errorMessage",
                    errorMessageService.getMessage("UNEXPECTED_ERROR")
            );
            return "redirect:/admin/users/" + id + "/edit";
        }


        String qPart = (q == null || q.isBlank()) ? "" : "q=" + q.trim();
        String rolePart = (role == null || role.isBlank()) ? "" : "role=" + role.trim();

        if (!qPart.isEmpty() && !rolePart.isEmpty()) return "redirect:/admin/users?" + qPart + "&" + rolePart;
        if (!qPart.isEmpty()) return "redirect:/admin/users?" + qPart;
        if (!rolePart.isEmpty()) return "redirect:/admin/users?" + rolePart;
        return "redirect:/admin/users";
    }


    @GetMapping("/admin/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String newUserForm(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("roles", roleRepo.findAll().stream().map(r -> r.getCode()).sorted().toList());
            model.addAttribute("activeDefault", true);
            return "users/users-admin-new";
        } catch (Exception e) {
            log.error("Failed to load new user form", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/admin/users";
        }
    }

    @PostMapping("/admin/users/new")
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(
            @RequestParam String firstName,
            @RequestParam String lastName,
            @RequestParam("newEmail") String email,
            @RequestParam String roleCode,
            @RequestParam("newPassword") String password,
            @RequestParam(required = false, defaultValue = "false") boolean active,
            RedirectAttributes redirectAttributes
    ) {
        try {
            createAdminUserService.create(new AdminUserEditRequest(firstName, lastName, email, roleCode, active, password));
            redirectAttributes.addFlashAttribute("successMessage", "User created successfully.");
            return "redirect:/admin/users";
        } catch (DuplicateEmailException e) {
            log.error("Duplicate email during user creation: {}", email, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), email);
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/admin/users/new";

        } catch (RoleNotFoundException e) {
            log.error("Role not found during user creation: {}", roleCode, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), roleCode);
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/admin/users/new";

        } catch (InvalidPasswordException e) {
            log.error("Invalid password during user creation", e);
            String message = errorMessageService.getMessage(e.getErrorCode());
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/admin/users/new";

        } catch (Exception e) {
            log.error("Create user failed. email={}, roleCode={}", email, roleCode, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("errorMessage", message);
            return "redirect:/admin/users/new";
        }
    }

    private String buildRedirectUrl(String q, String role) {
        String qPart = (q == null || q.isBlank()) ? "" : "q=" + q.trim();
        String rolePart = (role == null || role.isBlank()) ? "" : "role=" + role.trim();

        if (!qPart.isEmpty() && !rolePart.isEmpty())
            return "redirect:/admin/users?" + qPart + "&" + rolePart;
        if (!qPart.isEmpty())
            return "redirect:/admin/users?" + qPart;
        if (!rolePart.isEmpty())
            return "redirect:/admin/users?" + rolePart;
        return "redirect:/admin/users";
    }

}
