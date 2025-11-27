package at.fhv.Event.ui.controller;

import at.fhv.Event.application.equipment.GetAllEquipmentService;
import at.fhv.Event.application.event.*;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.EventEquipmentUpdateRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.application.user.UserPermissionService; // <--- NEU
import at.fhv.Event.domain.model.booking.BookingRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import at.fhv.Event.infrastructure.persistence.user.UserAccountJpaRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final CancelEventService cancelService;
    private final BookingRepository bookingRepository;
    private final UserAccountJpaRepository userRepo;
    private final UserPermissionService permissionService; // <--- NEU

    public EventController(CreateEventService createService,
                           UpdateEventService updateService,
                           GetEventDetailsService detailsService,
                           SearchEventService searchService,
                           FilterEventService filterService,
                           GetAllEquipmentService equipmentService,
                           CancelEventService cancelService,
                           BookingRepository bookingRepository,
                           UserAccountJpaRepository userRepo,
                           UserPermissionService permissionService) { // <--- Im Konstruktor hinzufügen

        this.createService = createService;
        this.updateService = updateService;
        this.detailsService = detailsService;
        this.searchService = searchService;
        this.filterService = filterService;
        this.equipmentService = equipmentService;
        this.cancelService = cancelService;
        this.bookingRepository = bookingRepository;
        this.userRepo = userRepo;
        this.permissionService = permissionService; // <--- Zuweisen
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new CreateEventRequest());
        model.addAttribute("equipments", equipmentService.getAll());
        model.addAttribute("eventEquipments", List.of());
        model.addAttribute("isEdit", false);
        return "events/create_event";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String create(@ModelAttribute("event") CreateEventRequest req,
                         RedirectAttributes redirect,
                         Authentication auth) {
        if (req.getDate() != null && req.getDate().isBefore(LocalDate.now())) {
            redirect.addFlashAttribute("error", "Event date cannot be in the past.");
            return "redirect:/events/new";
        }

        if (auth != null) {
            userRepo.findByEmailIgnoreCase(auth.getName()).ifPresent(u -> {
                req.setOrganizer(u.getFirstName() + " " + u.getLastName());
            });
        }

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
            default -> label;
        };
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRONT', 'ORGANIZER')")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirect,
                               Authentication auth) {
        try {
            EventDetailDTO detail = detailsService.getEventDetails(id);
            boolean expired = detail.date().isBefore(LocalDate.now());

            // --- ÄNDERUNG: NUTZUNG DES SERVICES ---
            if (!permissionService.canEdit(auth, detail)) {
                redirect.addFlashAttribute("error", "You are not allowed to edit this event.");
                return "redirect:/events/" + id;
            }

            if (Boolean.TRUE.equals(detail.cancelled())) {
                redirect.addFlashAttribute("error", "Event is already cancelled, you can't edit it anymore.");
                return "redirect:/events/" + id;
            }

            if (expired) {
                redirect.addFlashAttribute("error", "Event is already expired, you can't edit it anymore.");
                return "redirect:/events/" + id;
            }

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

            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", eqReqs);
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
    @PreAuthorize("hasAnyRole('ADMIN', 'FRONT', 'ORGANIZER')")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("event") UpdateEventRequest req,
                         RedirectAttributes redirect,
                         Authentication auth) {

        if (req.getDate() != null && req.getDate().isBefore(LocalDate.now())) {
            redirect.addFlashAttribute("error", "Event date cannot be in the past.");
            return "redirect:/events/" + id + "/edit";
        }

        updateService.updateEvent(id, req);
        redirect.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events";
    }

    @GetMapping
    public String list(Model model, Authentication auth) {
        List<EventOverviewDTO> events = filterService.filter(null, null, null, null, null, null, null, null, null);
        enrichEventsWithPermissions(model, auth);
        model.addAttribute("events", events);
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
            Model model,
            Authentication auth
    ) {
        if (q != null && q.length() > 75) {
            redirect.addFlashAttribute("error", "Search keyword must not exceed 75 characters.");
            return redirectBack(source);
        }

        List<EventOverviewDTO> events;
        if ("home".equals(source) && startDate != null && endDate == null) {
            events = filterService.filterExactDate(startDate, sort);
        } else {
            events = filterService.filter(q, category, location, difficulty, minPrice, maxPrice, startDate, endDate, sort);
        }

        enrichEventsWithPermissions(model, auth);

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
        model.addAttribute("now", LocalDateTime.now());
        return "events/list";
    }

    private String redirectBack(String source) {
        if ("home".equals(source)) return "redirect:/";
        return "redirect:/events";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") Long id, Model model, RedirectAttributes redirect, Authentication auth) {
        try {
            var event = detailsService.getEventDetails(id);
            LocalDateTime start = LocalDateTime.of(event.date(), event.startTime());
            boolean expired = start.isBefore(LocalDateTime.now());

            model.addAttribute("event", event);

            // --- ÄNDERUNG: NUTZUNG DES SERVICES ---
            model.addAttribute("canEdit", permissionService.canEdit(auth, event));
            model.addAttribute("canCancel", permissionService.canCancel(auth, event));

            int confirmed = bookingRepository.countSeatsForEvent(event.id());
            int baseSlots = event.maxParticipants() - event.minParticipants();
            int remaining = baseSlots - confirmed;
            if (remaining < 0) remaining = 0;

            model.addAttribute("remainingSpots", remaining);
            return "events/event_detail";

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Event not found.");
            return "redirect:/events";
        }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String cancelEvent(@PathVariable("id") Long id, RedirectAttributes redirect, Authentication auth) {
        try {
            EventDetailDTO detail = detailsService.getEventDetails(id);

            // --- ÄNDERUNG: NUTZUNG DES SERVICES ---
            if (!permissionService.canCancel(auth, detail)) {
                redirect.addFlashAttribute("error", "You are not allowed to cancel this event.");
                return "redirect:/events/" + id;
            }

            if (Boolean.TRUE.equals(detail.cancelled())) {
                redirect.addFlashAttribute("error", "Event is already cancelled.");
                return "redirect:/events/" + id;
            }

            cancelService.cancel(id);
            redirect.addFlashAttribute("success", "Event cancelled successfully!");
            return "redirect:/events/" + id;

        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Could not cancel event: " + e.getMessage());
            return "redirect:/events";
        }
    }

    private void enrichEventsWithPermissions(Model model, Authentication auth) {
        if (auth != null) {
            userRepo.findByEmailIgnoreCase(auth.getName()).ifPresent(u ->
                    model.addAttribute("currentUserName", u.getFirstName() + " " + u.getLastName())
            );
        }
    }
}