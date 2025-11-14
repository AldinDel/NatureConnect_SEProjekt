package at.fhv.Event.ui.controller;

import at.fhv.Event.application.equipment.GetAllEquipmentService;
import at.fhv.Event.application.event.*;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/events")
public class EventController {

    private final CreateEventService createService;
    private final UpdateEventService updateService;
    private final GetEventDetailsService detailsService;
    private final SearchEventService searchService;
    private final FilterEventService filterService;
    private final GetAllEquipmentService equipmentService;

    public EventController(CreateEventService createService,
                           UpdateEventService updateService,
                           GetEventDetailsService detailsService,
                           SearchEventService searchService,
                           FilterEventService filterService,
                           GetAllEquipmentService equipmentService) {
        this.createService = createService;
        this.updateService = updateService;
        this.detailsService = detailsService;
        this.searchService = searchService;
        this.filterService = filterService;
        this.equipmentService = equipmentService;
    }

    // CREATE FORM
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new CreateEventRequest());
        model.addAttribute("equipments", equipmentService.getAll());
        model.addAttribute("isEdit", false);
        return "events/create_event";
    }

    @PostMapping
    public String create(@ModelAttribute("event") CreateEventRequest req,
                         RedirectAttributes redirect) {
        createService.createEvent(req);
        redirect.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/events";
    }

    // EDIT FORM
    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id,
                               Model model,
                               RedirectAttributes redirect) {
        try {
            EventDetailDTO detail = detailsService.getEventDetails(id);

            UpdateEventRequest req = new UpdateEventRequest();
            req.setTitle(detail.title());
            req.setDescription(detail.description());
            req.setOrganizer(detail.organizer());
            req.setCategory(detail.category());
            req.setDate(detail.date());
            req.setStartTime(detail.startTime());
            req.setEndTime(detail.endTime());
            req.setLocation(detail.location());
            req.setDifficulty(detail.difficulty());
            req.setMinParticipants(detail.minParticipants());
            req.setMaxParticipants(detail.maxParticipants());
            req.setPrice(detail.price());
            req.setImageUrl(detail.imageUrl());
            req.setAudience(detail.audience());
            req.setRequiredEquipmentIds(detail.requiredEquipmentIds());
            req.setOptionalEquipmentIds(detail.optionalEquipmentIds());

            model.addAttribute("event", req);
            model.addAttribute("equipments", equipmentService.getAll());
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);

            return "events/create_event";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable Long id,
                         @ModelAttribute("event") UpdateEventRequest req,
                         RedirectAttributes redirect) {
        updateService.updateEvent(id, req);
        redirect.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events";
    }

    // LIST EVENTS
    @GetMapping
    public String list(Model model) {
        model.addAttribute("events", searchService.getAll());
        return "events/list";
    }

    // DETAILS
    @GetMapping("/{id}")
    public String details(@PathVariable Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("event", detailsService.getEventDetails(id));
            return "events/details";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }
    }
}
