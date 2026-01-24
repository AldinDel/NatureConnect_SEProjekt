package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.equipment.*;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.request.equipment.CreateEquipmentRequest;
import at.fhv.Event.application.request.equipment.UpdateEquipmentRequest;
import at.fhv.Event.domain.model.exception.EquipmentCreationException;
import at.fhv.Event.domain.model.exception.EquipmentNotFoundException;
import at.fhv.Event.presentation.rest.response.equipment.EquipmentDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private static final Logger logger = LoggerFactory.getLogger(EquipmentController.class);

    private final CreateEquipmentService createService;
    private final EditEquipmentService editService;
    private final DeleteEquipmentService deleteService;
    private final GetAllEquipmentService getAllService;
    private final GetEquipmentDetailsService getOneService;
    private final ErrorMessageService errorMessageService;

    public EquipmentController(CreateEquipmentService createService,
                               EditEquipmentService editService,
                               DeleteEquipmentService deleteService,
                               GetAllEquipmentService getAllService,
                               GetEquipmentDetailsService getOneService,
                               ErrorMessageService errorMessageService) {
        this.createService = createService;
        this.editService = editService;
        this.deleteService = deleteService;
        this.getAllService = getAllService;
        this.getOneService = getOneService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping
    public String list(Model model, RedirectAttributes redirectAttributes) {
        try {
            model.addAttribute("equipments", getAllService.getAll());
            return "equipment/list";
        } catch (Exception e) {
            logger.error("Failed to load equipment list", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/";
        }
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("createRequest", new CreateEquipmentRequest());
        return "equipment/create_equipment";
    }

    @PostMapping
    public String create(@ModelAttribute CreateEquipmentRequest req, Model model, RedirectAttributes redirect) {
        try {
            createService.create(req);
            redirect.addFlashAttribute("success", "Equipment created.");
            return "redirect:/equipment";
        } catch (EquipmentCreationException e) {
            logger.error("Equipment creation failed: {}", e.getMessage(), e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEquipmentName(), e.getReason());
            model.addAttribute("error", message);
            model.addAttribute("createRequest", req);
            return "equipment/create_equipment";
        } catch (IllegalArgumentException e) {
            logger.error("Invalid equipment data: {}", e.getMessage(), e);
            model.addAttribute("error", "Invalid equipment data: " + e.getMessage());
            model.addAttribute("createRequest", req);
            return "equipment/create_equipment";
        } catch (Exception e) {
            logger.error("Unexpected error creating equipment", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            model.addAttribute("error", message);
            model.addAttribute("createRequest", req);
            return "equipment/create_equipment";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            EquipmentDTO dto = getOneService.getById(id);
            UpdateEquipmentRequest req = new UpdateEquipmentRequest();
            req.setName(dto.name());
            req.setUnitPrice(dto.unitPrice());
            req.setRentable(dto.rentable());
            model.addAttribute("updateRequest", req);
            model.addAttribute("id", id);
            return "equipment/edit_equipment";
        } catch (EquipmentNotFoundException e) {
            logger.error("Equipment not found: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEquipmentId());
            redirect.addFlashAttribute("error", message);
            return "redirect:/equipment";

        } catch (Exception e) {
            logger.error("Failed to load equipment for editing: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirect.addFlashAttribute("error", message);
            return "redirect:/equipment";
        }
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute UpdateEquipmentRequest req,
                       RedirectAttributes redirect, Model model) {
        try {
            editService.edit(id, req);
            redirect.addFlashAttribute("success", "Equipment updated.");
            return "redirect:/equipment";
        } catch (EquipmentNotFoundException e) {
            logger.error("Equipment not found for update: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEquipmentId());
            redirect.addFlashAttribute("error", message);
            return "redirect:/equipment";

        } catch (IllegalArgumentException e) {
            logger.error("Invalid equipment update data: {}", e.getMessage(), e);
            model.addAttribute("error", "Invalid data: " + e.getMessage());
            model.addAttribute("updateRequest", req);
            model.addAttribute("id", id);
            return "equipment/edit_equipment";

        } catch (Exception e) {
            logger.error("Failed to update equipment: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            model.addAttribute("error", message);
            model.addAttribute("updateRequest", req);
            model.addAttribute("id", id);
            return "equipment/edit_equipment";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        try {
            deleteService.delete(id);
            redirect.addFlashAttribute("success", "Equipment deleted.");
            return "redirect:/equipment";
        } catch (EquipmentNotFoundException e) {
            logger.error("Equipment not found for deletion: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEquipmentId());
            redirect.addFlashAttribute("error", message);
            return "redirect:/equipment";

        } catch (IllegalStateException e) {
            logger.error("Cannot delete equipment: {}", e.getMessage(), e);
            redirect.addFlashAttribute("error", "Cannot delete equipment: " + e.getMessage());
            return "redirect:/equipment";

        } catch (Exception e) {
            logger.error("Failed to delete equipment: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirect.addFlashAttribute("error", message);
            return "redirect:/equipment";
        }
    }
}
