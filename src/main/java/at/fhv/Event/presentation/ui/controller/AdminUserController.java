package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.user.*;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import at.fhv.Event.application.request.user.AdminUserEditDTO;
import at.fhv.Event.application.request.user.AdminUserEditRequest;
import at.fhv.Event.infrastructure.persistence.user.RoleJpaRepository;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
public class AdminUserController {

    private final GetAdminUsersService getAdminUsersService;
    private final DeactivateUserService deactivateUserService;
    private final GetAdminUserForEditService getAdminUserForEditService;
    private final UpdateAdminUserService updateAdminUserService;
    private final RoleJpaRepository roleRepo;
    private final CreateAdminUserService createAdminUserService;

    public AdminUserController(
            GetAdminUsersService getAdminUsersService,
            DeactivateUserService deactivateUserService,
            GetAdminUserForEditService getAdminUserForEditService,
            UpdateAdminUserService updateAdminUserService,
            CreateAdminUserService createAdminUserService,
            RoleJpaRepository roleRepo
    ) {
        this.getAdminUsersService = getAdminUsersService;
        this.deactivateUserService = deactivateUserService;
        this.getAdminUserForEditService = getAdminUserForEditService;
        this.updateAdminUserService = updateAdminUserService;
        this.createAdminUserService = createAdminUserService;
        this.roleRepo = roleRepo;
    }




    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public String usersOverview(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "role", required = false) String role,
            Model model
    ) {
        String roleClean = (role == null || role.trim().isEmpty() || role.equalsIgnoreCase("all") || role.equalsIgnoreCase("all roles")) ? "" : role.trim();

        boolean hasQuery = q != null && !q.trim().isEmpty();
        boolean hasRole = !roleClean.isEmpty();

        if (!hasQuery && !hasRole) {
            model.addAttribute("users", getAdminUsersService.getLatestUsers(5));
        } else {
            model.addAttribute("users", getAdminUsersService.search(q, roleClean, 50));
        }

        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("role", roleClean.isEmpty() ? "all" : roleClean);

        return "users/users-admin-overview";
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
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage() == null ? "Action failed." : e.getMessage());
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
            Model model
    ) {
        AdminUserEditDTO user = getAdminUserForEditService.getById(id);

        model.addAttribute("user", user);
        model.addAttribute("roles", roleRepo.findAll().stream().map(r -> r.getCode()).sorted().toList());
        model.addAttribute("q", q == null ? "" : q);
        model.addAttribute("roleFilter", role == null ? "all" : role);

        return "users/users-admin-edit";
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
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    e.getMessage() == null ? "Update failed." : e.getMessage());
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
    public String newUserForm(Model model) {
        model.addAttribute("roles", roleRepo.findAll().stream().map(r -> r.getCode()).sorted().toList());
        model.addAttribute("activeDefault", true);
        return "users/users-admin-new";
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
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage() == null ? "Create failed." : e.getMessage());
            return "redirect:/admin/users/new";
        }
    }



}
