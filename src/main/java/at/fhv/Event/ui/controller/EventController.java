package at.fhv.Event.ui.controller;

import at.fhv.Event.application.equipment.GetAllEquipmentService;
import at.fhv.Event.application.event.*;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.EventEquipmentUpdateRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {

    private final CreateEventService createService;
    private final UpdateEventService updateService;
    private final GetEventDetailsService detailsService;
    private final SearchEventService searchService;
    private final FilterEventService filterService;
    private final GetAllEquipmentService equipmentService;
    private final CancelEventService cancelService  ;

    public EventController(CreateEventService createService,
                           UpdateEventService updateService,
                           GetEventDetailsService detailsService,
                           SearchEventService searchService,
                           FilterEventService filterService,
                           GetAllEquipmentService equipmentService,
                           CancelEventService cancelService) {
        this.createService = createService;
        this.updateService = updateService;
        this.detailsService = detailsService;
        this.searchService = searchService;
        this.filterService = filterService;
        this.equipmentService = equipmentService;
        this.cancelService = cancelService;
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new CreateEventRequest());
        model.addAttribute("equipments", equipmentService.getAll());
        model.addAttribute("eventEquipments", List.of());
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

    private String mapAudienceLabelToEnumName(String label) {
        if (label == null) return null;
        return switch (label) {
            case "Individuals, Groups, Companies" -> "INDIVIDUALS_GROUPS_COMPANIES";
            case "Groups, Companies"            -> "GROUPS_COMPANIES";
            case "Individuals only"             -> "INDIVIDUALS_ONLY";
            case "Companies only"               -> "COMPANIES_ONLY";
            default -> label; // Fallback
        };
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id,
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
            req.setAudience(mapAudienceLabelToEnumName(detail.audience()));

            // Map equipment
            List<EventEquipmentUpdateRequest> eqReqs = detail.equipments().stream().map(eq -> {
                EventEquipmentUpdateRequest r = new EventEquipmentUpdateRequest();
                r.setId(eq.id());
                r.setName(eq.name());
                r.setUnitPrice(eq.unitPrice());
                r.setRentable(eq.rentable());
                r.setRequired(eq.required());
                r.setStock(eq.stock());
                return r;
            }).toList();

            req.setEquipments(eqReqs);
            model.addAttribute("eventEquipments", eqReqs);
            System.out.println("=== Setting eventEquipments in model ===");
            System.out.println("eventEquipments size: " + eqReqs.size());
            eqReqs.forEach(eq -> System.out.println("  - " + eq.getId() + ": " + eq.getName()));
            System.out.println("====================================");

            // DEBUG - IMPORTANT: Check if equipment list is set
            System.out.println("=== CONTROLLER showEditForm DEBUG ===");
            System.out.println("UpdateEventRequest.equipments size: " + req.getEquipments().size());
            req.getEquipments().forEach(e ->
                    System.out.println("  Equipment: id=" + e.getId() + ", name=" + e.getName())
            );
            System.out.println("=====================================");

            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", eqReqs);  // ← ADD THIS LINE - pass equipment separately
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);

            return "events/create_event";

        } catch (Exception e) {
            e.printStackTrace();
            redirect.addFlashAttribute("error", "Event not found: " + e.getMessage());
            return "redirect:/events";
        }
    }

    @PostMapping("/{id}")
    public String update(@PathVariable("id") Long id,
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

    @GetMapping("/search")
    public String searchEvents(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String sort,
            @RequestParam(required = false) String source,
            RedirectAttributes redirect,
            Model model
    ) {
        // validation
        if (q != null && q.length() > 75) {
            redirect.addFlashAttribute("error", "Search keyword must not exceed 75 characters.");
            return redirectBack(source);
        }

        if (location != null && location.length() > 75) {
            redirect.addFlashAttribute("error", "Location cannot exceed 75 characters.");
            return redirectBack(source);
        }

        if (startDate != null && startDate.isBefore(LocalDate.now())) {
            redirect.addFlashAttribute("error", "Start date cannot be in the past.");
            return redirectBack(source);
        }

        if (endDate != null && endDate.isBefore(LocalDate.now())) {
            redirect.addFlashAttribute("error", "End date cannot be in the past.");
            return redirectBack(source);
        }

        if (startDate != null && endDate != null && endDate.isBefore(startDate)) {
            redirect.addFlashAttribute("error", "End date cannot be before start date.");
            return redirectBack(source);
        }

        if (minPrice != null && maxPrice != null && maxPrice.compareTo(minPrice) < 0) {
            redirect.addFlashAttribute("error", "Maximum price cannot be lower than minimum price.");
            return redirectBack(source);
        }

        List<EventOverviewDTO> events;
        if ("home".equals(source) && startDate != null && endDate == null) {
            events = filterService.filterExactDate(startDate, sort);
        } else {
            events = filterService.filter(
                    q, category, location, difficulty,
                    minPrice, maxPrice,
                    startDate, endDate,
                    sort
            );
        }

        model.addAttribute("events", events);
        model.addAttribute("param", new Object() {
            public final String qv = q;
            public final String categoryv = category;
            public final String locationv = location;
            public final String difficultyv = difficulty;
            public final BigDecimal minPricev = minPrice;
            public final BigDecimal maxPricev = maxPrice;
            public final LocalDate startDatev = startDate;
            public final LocalDate endDatev = endDate;
        });
        model.addAttribute("sort", sort);
        return "events/list";
    }

    private String redirectBack(String source) {
        if ("home".equals(source)) {
            return "redirect:/";
        }
        return "redirect:/events";
    }

    // DETAILS
    @GetMapping("/{id}")
    public String details(@PathVariable("id") Long id, Model model, RedirectAttributes redirect) {
        try {
            model.addAttribute("event", detailsService.getEventDetails(id));
            return "events/event_detail";
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }
    }

    // Cancel Event Endpoint
    @PostMapping("/{id}/cancel")
    public String cancelEvent(@PathVariable("id") Long id, RedirectAttributes redirect) {
        try {
            cancelService.cancel(id);
            redirect.addFlashAttribute("success", "Event cancelled successfully!");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Could not cancel event: " + e.getMessage());
        }
        return "redirect:/events";
    }

    // Backoffice View - alle Events inkl. cancelled
    @GetMapping("/backoffice")
    public String backofficeList(Model model) {
        model.addAttribute("events", searchService.getAllIncludingCancelled());
        model.addAttribute("showCancelled", true);
        return "events/list";
    }
}