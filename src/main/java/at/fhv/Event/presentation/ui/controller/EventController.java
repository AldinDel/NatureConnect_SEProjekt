package at.fhv.Event.presentation.ui.controller;

import at.fhv.Event.application.equipment.GetAllEquipmentService;
import at.fhv.Event.application.event.*;
import at.fhv.Event.application.exception.ErrorMessageService;
import at.fhv.Event.application.request.event.CreateEventRequest;
import at.fhv.Event.application.request.event.EventEquipmentUpdateRequest;
import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.application.user.UserPermissionService;
import at.fhv.Event.domain.model.exception.EventNotFoundException;
import at.fhv.Event.domain.model.exception.EventValidationException;
import at.fhv.Event.domain.model.exception.ImageUploadException;
import at.fhv.Event.presentation.rest.response.event.EventDetailDTO;
import at.fhv.Event.presentation.rest.response.event.EventOverviewDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Controller
@RequestMapping("/events")
public class EventController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private final CreateEventService createService;
    private final UpdateEventService updateService;
    private final GetEventDetailsService detailsService;
    private final FilterEventService filterService;
    private final GetAllEquipmentService equipmentService;
    private final CancelEventService cancelService;
    private final UserPermissionService userPermissionService;
    private final EventAccessService accessService;
    private final CloudinaryService cloudinaryService;
    private final ErrorMessageService errorMessageService;


    public EventController(CreateEventService createService,
                           UpdateEventService updateService,
                           GetEventDetailsService detailsService,
                           FilterEventService filterService,
                           GetAllEquipmentService equipmentService,
                           CancelEventService cancelService,
                           UserPermissionService userPermissionService,
                           EventAccessService accessService,
                           CloudinaryService cloudinaryService,
                           ErrorMessageService errorMessageService) {

        this.createService = createService;
        this.updateService = updateService;
        this.detailsService = detailsService;
        this.filterService = filterService;
        this.equipmentService = equipmentService;
        this.cancelService = cancelService;
        this.userPermissionService = userPermissionService;
        this.accessService = accessService;
        this.cloudinaryService = cloudinaryService;
        this.errorMessageService = errorMessageService;
    }

    @GetMapping("/new")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String showCreateForm(Model model, Authentication auth) {
        CreateEventRequest request = new CreateEventRequest();
        if (auth != null && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZER"))) {
            userPermissionService.getUserFullName(auth).ifPresent(request::setOrganizer);
        }
        model.addAttribute("event", request);
        model.addAttribute("equipments", equipmentService.getAll());
        model.addAttribute("eventEquipments", request.getEquipments());
        model.addAttribute("isEdit", false);
        return "events/create_event";
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String create(@ModelAttribute("event") CreateEventRequest req,
                         @RequestParam("photo") MultipartFile photo,
                         RedirectAttributes redirect,
                         Authentication auth, Model model) {
        try {
            if (req.getDate() != null && req.getDate().isBefore(LocalDate.now())) {
                redirect.addFlashAttribute("error", "Event date cannot be in the past.");
                return "redirect:/events/new";
            }

            if (auth != null && !auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))
                    && auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ORGANIZER"))) {
                userPermissionService.getUserFullName(auth).ifPresent(req::setOrganizer);
            }

            String imageUrl = cloudinaryService.uploadImage(photo);

            if (imageUrl == null && photo != null && !photo.isEmpty()) {
                // Upload wurde versucht, aber ist fehlgeschlagen
                redirect.addFlashAttribute("error", "Image upload failed.");
                return "redirect:/events/new";
            }

            if (imageUrl != null) {
                req.setImageUrl(imageUrl);
            }

            createService.createEvent(req);
            redirect.addFlashAttribute("success", "Event created successfully!");
            return "redirect:/events";

        } catch (ImageUploadException e) {
            logger.error("Image upload failed during event creation", e);
            String message = errorMessageService.getMessage(e.getErrorCode());
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("equipments", equipmentService.getAll());
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", false);
            return "events/create_event";

        } catch (EventValidationException e) {
            logger.error("Event validation failed", e);
            String message = errorMessageService.getMessage(e.getErrorCode());
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("equipments", equipmentService.getAll());
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", false);
            return "events/create_event";

        } catch (Exception e) {
            logger.error("Failed to create event", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("equipments", equipmentService.getAll());
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", false);
            return "events/create_event";
        }
    }

    @GetMapping("/{id}/edit")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRONT', 'ORGANIZER')")
    public String showEditForm(@PathVariable("id") Long id,
                               Model model,
                               RedirectAttributes redirect,
                               Authentication auth) {
        try {
            EventDetailDTO detail = detailsService.getEventDetails(id);
            if (!userPermissionService.canEdit(auth, detail)) {
                redirect.addFlashAttribute("error", "You are not allowed to edit this event.");
                return "redirect:/events/" + id;
            }

            if (Boolean.TRUE.equals(detail.cancelled())) {
                redirect.addFlashAttribute("error", "Event is already cancelled, you can't edit it anymore.");
                return "redirect:/events/" + id;
            }

            if (accessService.isEventExpired(detail)) {
                redirect.addFlashAttribute("error", "Event is already expired, you can't edit it anymore.");
                return "redirect:/events/" + id;
            }

            UpdateEventRequest req = buildUpdateRequest(detail);
            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);

            return "events/create_event";
        } catch (EventNotFoundException e) {
            logger.error("Event not found for editing: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirect.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (Exception e) {
            logger.error("Failed to load event for editing: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirect.addFlashAttribute("error", message);
            return "redirect:/events";
        }
    }

    @PostMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'FRONT', 'ORGANIZER')")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("event") UpdateEventRequest req,
                         @RequestParam(value = "photo", required = false) MultipartFile photo,
                         RedirectAttributes redirect,
                         Authentication auth, Model model) {

        try {
            if (!req.isRecurring()
                    && req.getDate() != null
                    && req.getDate().isBefore(LocalDate.now())) {

                redirect.addFlashAttribute("error", "Event date cannot be in the past.");
                return "redirect:/events/" + id + "/edit";
            }

            if (photo != null && !photo.isEmpty()) {
                String imageUrl = cloudinaryService.uploadImage(photo);

                if (imageUrl == null) {
                    redirect.addFlashAttribute("error", "Image upload failed.");
                    return "redirect:/events/" + id + "/edit";
                }

                req.setImageUrl(imageUrl);

            }

            updateService.updateEvent(id, req);
            redirect.addFlashAttribute("success", "Event updated successfully!");
            return "redirect:/events";
        } catch (EventNotFoundException e) {
            logger.error("Event not found for update: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirect.addFlashAttribute("error", message);
            return "redirect:/events";

        } catch (ImageUploadException e) {
            logger.error("Image upload failed during event update", e);
            String message = errorMessageService.getMessage(e.getErrorCode());
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);
            return "events/create_event";

        } catch (EventValidationException e) {
            logger.error("Event validation failed during update", e);
            String message = errorMessageService.getMessage(e.getErrorCode());
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);
            return "events/create_event";

        } catch (Exception e) {
            logger.error("Failed to update event {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            model.addAttribute("error", message);
            model.addAttribute("event", req);
            model.addAttribute("eventEquipments", req.getEquipments());
            model.addAttribute("isEdit", true);
            model.addAttribute("id", id);
            return "events/create_event";
        }

    }

    @GetMapping
    public String list(Model model, Authentication auth, RedirectAttributes redirectAttributes) {
        try {
            List<EventOverviewDTO> events = filterService.filter(
                    null, null, null, null, null, null, null, null, null
            );

            events = accessService.filterVisibleEvents(events, auth);
            addUserContextToModel(model, auth);
            model.addAttribute("events", events);
            return "events/list";
        } catch (Exception e) {
            logger.error("Failed to load events list", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/";
        }
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
            Model model,
            Authentication auth, RedirectAttributes redirectAttributes
    ) {
        try {
            List<EventOverviewDTO> events;
            if ("home".equals(source) && startDate != null && endDate == null) {
                events = filterService.filterExactDate(startDate, sort);
            } else {
                events = filterService.filter(q, category, location, difficulty, minPrice, maxPrice, startDate, endDate, sort);
            }

            events = accessService.filterVisibleEvents(events, auth);

            addUserContextToModel(model, auth);
            model.addAttribute("events", events);
            model.addAttribute("param", createSearchParams(q, category, location, difficulty, minPrice, maxPrice, startDate, endDate));
            model.addAttribute("sort", sort);
            model.addAttribute("now", LocalDate.now());
            return "events/list";
        } catch (Exception e) {
            logger.error("Failed to search events", e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirectAttributes.addFlashAttribute("error", message);
            return "redirect:/events";
        }
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") Long id, Model model, RedirectAttributes redirect, Authentication auth) {
        try {
            EventDetailDTO event = detailsService.getEventDetails(id);

        model.addAttribute("event", event);
        model.addAttribute("canEdit", userPermissionService.canEdit(auth, event));
        model.addAttribute("canCancel", userPermissionService.canCancel(auth, event));
        int remaining = accessService.calculateRemainingSpots(event.id(), event.minParticipants(), event.maxParticipants());
        model.addAttribute("remainingSpots", remaining);

        boolean expired = accessService.isEventExpired(event);
        model.addAttribute("expired", expired);

        boolean isHiking = event.category() != null && event.category().toLowerCase().contains("hiking");
        model.addAttribute("isHikingEvent", isHiking);

        return "events/event_detail";
    } catch (EventNotFoundException e) {
        logger.error("Event not found: {}", id, e);
        String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
        redirect.addFlashAttribute("error", message);
        return "redirect:/events";

    } catch (Exception e) {
        logger.error("Failed to load event details: {}", id, e);
        String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
        redirect.addFlashAttribute("error", message);
        return "redirect:/events";
    }
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("hasAnyRole('ADMIN', 'ORGANIZER')")
    public String cancelEvent(@PathVariable("id") Long id,
                              @RequestParam("reason") String reason,
                              RedirectAttributes redirect,
                              Authentication auth) {

        try {
            EventDetailDTO detail = detailsService.getEventDetails(id);

            if (!userPermissionService.canCancel(auth, detail)) {
                redirect.addFlashAttribute("error", "You are not allowed to cancel this event.");
                return "redirect:/events/" + id;
            }

            if (Boolean.TRUE.equals(detail.cancelled())) {
                redirect.addFlashAttribute("error", "Event is already cancelled.");
                return "redirect:/events/" + id;
            }

            if (accessService.isEventExpired(detail)) {
                redirect.addFlashAttribute("error", "Expired events cannot be cancelled.");
                return "redirect:/events/" + id;
            }
            cancelService.cancel(id, reason);
            String message = errorMessageService.getMessage("EVENT_CANCELLED_SUCCESS");
            redirect.addFlashAttribute("success", message);
        } catch (EventNotFoundException e) {
            logger.error("Event not found for cancellation: {}", id, e);
            String message = errorMessageService.getMessage(e.getErrorCode(), e.getEventId());
            redirect.addFlashAttribute("error", message);

        } catch (Exception e) {
            logger.error("Failed to cancel event: {}", id, e);
            String message = errorMessageService.getMessage("UNEXPECTED_ERROR");
            redirect.addFlashAttribute("error", message);
        }

        return "redirect:/events/" + id;
    }

    @GetMapping("/{id}/refund-count")
    @ResponseBody
    public long refundableCount(@PathVariable Long id) {
        return cancelService.getRefundableCount(id);
    }

    private UpdateEventRequest buildUpdateRequest(EventDetailDTO detail) {
        UpdateEventRequest req = new UpdateEventRequest();
        req.setTitle(detail.title());
        req.setDescription(detail.description());
        req.setOrganizer(detail.organizer());
        req.setCategory(detail.category());
        req.setRecurring(detail.recurring());
        req.setRecurrenceStart(detail.recurrenceStart());
        req.setRecurrenceEnd(detail.recurrenceEnd());
        if (detail.recurrenceDays() != null) {
            req.setRecurrenceDays(new HashSet<>(detail.recurrenceDays()));
        }
        req.setDate(detail.date());
        req.setEndDate(detail.endDate());
        req.setStartTime(detail.startTime());
        req.setEndTime(detail.endTime());
        req.setLocation(detail.location());
        req.setDifficulty(detail.difficulty());
        req.setMinParticipants(detail.minParticipants());
        req.setMaxParticipants(detail.maxParticipants());
        req.setPrice(detail.price());
        req.setImageUrl(detail.imageUrl());
        req.setAudience(mapAudienceLabelToEnumName(detail.audience()));
        req.setHikeRouteKeys(detail.hikeRouteKeys());

        List<EventEquipmentUpdateRequest> eqReqs = new ArrayList<>();
        for (var eq : detail.equipments()) {
            EventEquipmentUpdateRequest r = new EventEquipmentUpdateRequest();
            r.setId(eq.id());
            r.setName(eq.name());
            r.setUnitPrice(eq.unitPrice());
            r.setRentable(eq.rentable());
            r.setRequired(eq.required());
            r.setStock(eq.stock());
            eqReqs.add(r);
        }
        req.setEquipments(eqReqs);
        return req;
    }

    private String mapAudienceLabelToEnumName(String label) {
        if (label == null) {
            return null;
        }

        if (label.equals("Individuals, Groups, Companies")) {
            return "INDIVIDUALS_GROUPS_COMPANIES";
        }
        if (label.equals("Groups, Companies")) {
            return "GROUPS_COMPANIES";
        }
        if (label.equals("Individuals only")) {
            return "INDIVIDUALS_ONLY";
        }
        if (label.equals("Companies only")) {
            return "COMPANIES_ONLY";
        }

        return label;
    }

    private void addUserContextToModel(Model model, Authentication auth) {
        String userName = accessService.getCurrentUserFullName(auth);
        if (userName != null) {
            model.addAttribute("currentUserName", userName);
        }
    }

    private String redirectBack(String source) {
        if ("home".equals(source)) {
            return "redirect:/";
        }
        return "redirect:/events";
    }

    private Object createSearchParams(String q, String category, String location,
                                      String difficulty, BigDecimal minPrice,
                                      BigDecimal maxPrice, LocalDate startDate,
                                      LocalDate endDate) {
        return new Object() {
            public final String qv = q;
            public final String categoryv = category;
            public final String locationv = location;
            public final String difficultyv = difficulty;
            public final BigDecimal minPricev = minPrice;
            public final BigDecimal maxPricev = maxPrice;
            public final LocalDate startDatev = startDate;
            public final LocalDate endDatev = endDate;
        };
    }
}