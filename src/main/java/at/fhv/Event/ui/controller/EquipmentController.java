package at.fhv.Event.ui.controller;

import at.fhv.Event.application.equipment.*;
import at.fhv.Event.application.request.equipment.CreateEquipmentRequest;
import at.fhv.Event.application.request.equipment.UpdateEquipmentRequest;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/equipment")
public class EquipmentController {

    private final CreateEquipmentService createService;
    private final EditEquipmentService editService;
    private final DeleteEquipmentService deleteService;
    private final GetAllEquipmentService getAllService;
    private final GetEquipmentDetailsService getOneService;

    public EquipmentController(CreateEquipmentService createService,
                               EditEquipmentService editService,
                               DeleteEquipmentService deleteService,
                               GetAllEquipmentService getAllService,
                               GetEquipmentDetailsService getOneService) {
        this.createService = createService;
        this.editService = editService;
        this.deleteService = deleteService;
        this.getAllService = getAllService;
        this.getOneService = getOneService;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("equipments", getAllService.getAll());
        return "equipment/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("createRequest", new CreateEquipmentRequest());
        return "equipment/create_equipment";
    }

    @PostMapping
    public String create(@ModelAttribute CreateEquipmentRequest req, RedirectAttributes redirect) {
        createService.create(req);
        redirect.addFlashAttribute("success", "Equipment created.");
        return "redirect:/equipment";
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
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Equipment not found.");
            return "redirect:/equipment";
        }
    }

    @PostMapping("/{id}")
    public String edit(@PathVariable Long id,
                       @ModelAttribute UpdateEquipmentRequest req,
                       RedirectAttributes redirect) {
        editService.edit(id, req);
        redirect.addFlashAttribute("success", "Equipment updated.");
        return "redirect:/equipment";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes redirect) {
        deleteService.delete(id);
        redirect.addFlashAttribute("success", "Equipment deleted.");
        return "redirect:/equipment";
    }
}
