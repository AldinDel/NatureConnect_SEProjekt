package at.fhv.Authors.controller;

import at.fhv.Authors.domain.model.Difficulty;
import at.fhv.Authors.domain.model.Event;
import at.fhv.Authors.domain.model.Equipment;
import at.fhv.Authors.domain.model.EventEquipment;
import at.fhv.Authors.domain.model.EventEquipmentId;
import at.fhv.Authors.persistence.EquipmentRepository;
import at.fhv.Authors.persistence.EventEquipmentRepository;
import at.fhv.Authors.persistence.EventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.Valid;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.Map;


@Controller
public class EventController {

    private final EventRepository repo;

    @Autowired
    private EquipmentRepository equipmentRepo;

    @Autowired
    private EventEquipmentRepository eventEquipmentRepo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }
    /*
    wurde ersetzt (ganz unten)
    @GetMapping("/events/search")
    public String search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String activity,
            Model model) {

        // temporäre ausgabe
        System.out.println("Suchparameter: " + q + ", " + location + ", " + date + ", " + activity);

        // dummy-daten (nur zu testzwecken)
        model.addAttribute("events", java.util.Collections.emptyList());

        return "events/list";
    }
    */

    // Formular zum Erstellen anzeigen
    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("isEdit", false);
        return "events/create_event";
    }

    // Formular zum Editieren anzeigen
    @GetMapping("/events/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Event event = repo.findById(id).orElse(null);
        if (event == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }
        model.addAttribute("event", event);
        model.addAttribute("isEdit", true);
        return "events/create_event";
    }

    // Event erstellen
    @PostMapping("/events")
    public String create(
            @ModelAttribute("event") Event event,
            @RequestParam(value = "photo", required = false) MultipartFile photo,
            @RequestParam(value = "equipmentNames", required = false) List<String> equipmentNames,
            @RequestParam(value = "equipmentPrices", required = false) List<BigDecimal> equipmentPrices,
            @RequestParam(value = "equipmentRentable", required = false) List<String> equipmentRentableFlags,
            @RequestParam(value = "requiredFlags", required = false) List<String> requiredFlags,
            RedirectAttributes ra) throws IOException {


        // Validierung: End-Zeit muss nach Start-Zeit sein
        if (event.getEndTime() != null && event.getStartTime() != null) {
            if (!event.getEndTime().isAfter(event.getStartTime())) {
                ra.addFlashAttribute("error", "End time must be after start time!");
                return "redirect:/events/new";
            }
        }

        // Standardwerte setzen falls null
        if (event.getMinParticipants() == null) {
            event.setMinParticipants(1);
        }
        if (event.getPrice() == null) {
            event.setPrice(BigDecimal.ZERO);
        }

        // Bild speichern (optional)
        if (photo != null && !photo.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            System.out.println("UPLOAD DIR → " + uploadDir.toAbsolutePath());
            Files.createDirectories(uploadDir);


            // --- Erweiterung robust bestimmen ---
            String originalName = photo.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);

            if (ext == null || ext.isBlank()) {
                String contentType = photo.getContentType(); // z. B. image/avif
                if (contentType != null && contentType.startsWith("image/")) {
                    ext = contentType.substring(6); // ergibt "avif", "jpeg", etc.
                } else {
                    ext = "jpg"; // Fallback
                }
            }

            // --- Alles auf lowercase, um Fehler zu vermeiden ---
            ext = ext.toLowerCase();

            // --- Dateiname generieren ---
            String filename = UUID.randomUUID().toString() + "." + ext;
            Path target = uploadDir.resolve(filename);

            try (var in = photo.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            // --- Nur relativen Pfad speichern! ---
            event.setImageUrl("/uploads/" + filename);
        }

        //Event speichern
        repo.save(event);

        // --- Equipment speichern & verknüpfen ---
        if (equipmentNames != null && !equipmentNames.isEmpty()) {
            for (int i = 0; i < equipmentNames.size(); i++) {
                String name = equipmentNames.get(i);
                if (name == null || name.trim().isEmpty()) continue;

                BigDecimal price = (equipmentPrices != null && equipmentPrices.size() > i && equipmentPrices.get(i) != null)
                        ? equipmentPrices.get(i)
                        : BigDecimal.ZERO;

                boolean rentable = (equipmentRentableFlags != null && equipmentRentableFlags.size() > i);
                boolean required = (requiredFlags != null && requiredFlags.size() > i);

                // Prüfen, ob Equipment schon existiert
                Equipment eq = equipmentRepo.findByNameIgnoreCase(name.trim())
                        .orElseGet(() -> {
                            Equipment newEq = new Equipment();
                            newEq.setName(name.trim());
                            newEq.setUnitPrice(price);
                            newEq.setRentable(rentable);
                            return equipmentRepo.save(newEq);
                        });

                // Verknüpfung Event <-> Equipment speichern
                EventEquipment link = new EventEquipment(event.getId(), eq.getId(), required);
                eventEquipmentRepo.save(link);
            }
        }

        ra.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/events";
    }

    // Event updaten
    @PostMapping("/events/{id}")
    public String update(@PathVariable("id") Long id,
                         @ModelAttribute("event") Event updatedEvent,
                         @RequestParam(value = "photo", required = false) MultipartFile photo,
                         RedirectAttributes ra) throws IOException {

        Event existingEvent = repo.findById(id).orElse(null);
        if (existingEvent == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        // Validierung: End-Zeit muss nach Start-Zeit sein
        if (updatedEvent.getEndTime() != null && updatedEvent.getStartTime() != null) {
            if (!updatedEvent.getEndTime().isAfter(updatedEvent.getStartTime())) {
                ra.addFlashAttribute("error", "End time must be after start time!");
                return "redirect:/events/" + id + "/edit";
            }
        }

        // Standardwerte setzen falls null
        if (updatedEvent.getMinParticipants() == null) {
            updatedEvent.setMinParticipants(1);
        }
        if (updatedEvent.getPrice() == null) {
            updatedEvent.setPrice(BigDecimal.ZERO);
        }


        // WICHTIG: Kopiere alle Felder vom updatedEvent zum existingEvent
        existingEvent.setTitle(updatedEvent.getTitle());
        existingEvent.setDescription(updatedEvent.getDescription());
        existingEvent.setOrganizer(updatedEvent.getOrganizer());
        existingEvent.setCategory(updatedEvent.getCategory());
        existingEvent.setDate(updatedEvent.getDate());
        existingEvent.setStartTime(updatedEvent.getStartTime());
        existingEvent.setEndTime(updatedEvent.getEndTime());
        existingEvent.setLocation(updatedEvent.getLocation());
        existingEvent.setDifficulty(updatedEvent.getDifficulty());
        existingEvent.setMinParticipants(updatedEvent.getMinParticipants());
        existingEvent.setMaxParticipants(updatedEvent.getMaxParticipants());
        existingEvent.setPrice(updatedEvent.getPrice());

        // Bild: Behalte altes Bild, wenn kein neues hochgeladen wird
        if (photo != null && !photo.isEmpty()) {
            Path uploadDir = Paths.get("uploads");
            System.out.println("UPLOAD DIR → " + uploadDir.toAbsolutePath());
            Files.createDirectories(uploadDir);

            // --- Erweiterung robust bestimmen ---
            String originalName = photo.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);

            if (ext == null || ext.isBlank()) {
                String contentType = photo.getContentType(); // z. B. image/avif
                if (contentType != null && contentType.startsWith("image/")) {
                    ext = contentType.substring(6); // ergibt "avif", "jpeg", etc.
                } else {
                    ext = "jpg"; // Fallback
                }
            }

            // --- Alles auf lowercase, um Fehler zu vermeiden ---
            ext = ext.toLowerCase();

            // --- Dateiname generieren ---
            String filename = UUID.randomUUID().toString() + "." + ext;
            Path target = uploadDir.resolve(filename);

            try (var in = photo.getInputStream()) {
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING);
            }

            // --- Nur relativen Pfad speichern! ---
            existingEvent.setImageUrl("/uploads/" + filename);
        }

        // Falls kein neues Bild: behalte das alte (bereits in existingEvent)

        repo.save(existingEvent);
        ra.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events";
    }

    // Ab Hier Overview
    @GetMapping("/events/overview")
    public String overviewAlias() {
        return "redirect:/events";
    }

    @GetMapping("/events")
    public String showAllEvents(Model model) {
        List<Event> events = repo.findAll();
        model.addAttribute("events", events);
        return "events/list";
    }

    @GetMapping("/events/search")

    public String searchEvents(

            @RequestParam(required = false) String q,

            @RequestParam(required = false) String location,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) String difficulty,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String activity,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sort,
            Model model
    ) {
        List<Event> results = repo.findAll();
        if (q != null) {
            String trimmed = q.trim();
            if (!trimmed.isEmpty()) {
                if (trimmed.length() < 3) {
                    model.addAttribute("error", "Search query must be at least 3 characters long.");
                    model.addAttribute("events", List.of());
                    return "events/list";
                } else {
                    String keyword = trimmed.toLowerCase();
                    results = results.stream()
                            .filter(e -> {
                                String title = e.getTitle() != null ? e.getTitle().toLowerCase() : "";
                                String desc = e.getDescription() != null ? e.getDescription().toLowerCase() : "";
                                return title.contains(keyword) || desc.contains(keyword);
                            })
                            .toList();
                }
            }
        }


        // Location
        if (location != null && !location.isBlank()) {
            results = results.stream()
                    .filter(e -> e.getLocation() != null &&
                            e.getLocation().toLowerCase().contains(location.toLowerCase()))
                    .toList();
        }

        // Difficulty
        if (difficulty != null && !difficulty.isBlank()) {
            try {
                Difficulty filterValue = Difficulty.valueOf(difficulty.toUpperCase());
                results = results.stream()
                        .filter(e -> e.getDifficulty() == filterValue)
                        .toList();
            } catch (IllegalArgumentException ex) {
            }
        }

        // Date
        if (startDate != null || endDate != null) {
            results = results.stream()
                    .filter(e -> {
                        LocalDate eventDate = e.getDate();
                        if (eventDate == null) return false;

                        // Nur startDate (z. B. Home)
                        if (endDate == null && startDate != null) {
                            // Nur Events, die an genau diesem Datum sind
                            return eventDate.isEqual(startDate);
                        }

                        // Nur endDate (selten)
                        if (startDate == null && endDate != null) {
                            return !eventDate.isAfter(endDate);
                        }

                        // Beide gesetzt (Overview)
                        if (startDate != null && endDate != null) {
                            return !eventDate.isBefore(startDate) && !eventDate.isAfter(endDate);
                        }

                        return true;
                    })
                    .toList();
        }

        // Price range
        if (minPrice != null || maxPrice != null) {
            results = results.stream()
                    .filter(e -> {
                        if (e.getPrice() == null) return false;

                        BigDecimal price = e.getPrice();
                        boolean aboveMin = (minPrice == null || price.compareTo(BigDecimal.valueOf(minPrice)) >= 0);
                        boolean belowMax = (maxPrice == null || price.compareTo(BigDecimal.valueOf(maxPrice)) <= 0);

                        return aboveMin && belowMax;
                    })
                    .toList();
        }

        // Activity
        if (activity != null && !activity.isBlank()) {
            results = results.stream()
                    .filter(e -> e.getCategory() != null &&
                            e.getCategory().equalsIgnoreCase(activity))
                    .toList();
        }

        // Category
        if (category != null && !category.isBlank()) {
            results = results.stream()
                    .filter(e -> e.getCategory() != null &&
                            e.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        // Sortieren
        if (sort != null && !sort.isBlank()) {
            switch (sort) {
                case "dateAsc" -> results.sort(Comparator.comparing(Event::getDate, Comparator.nullsLast(Comparator.naturalOrder())));
                case "dateDesc" -> results.sort(Comparator.comparing(Event::getDate, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
                case "priceAsc" -> results.sort(Comparator.comparing(Event::getPrice, Comparator.nullsLast(Comparator.naturalOrder())));
                case "priceDesc" -> results.sort(Comparator.comparing(Event::getPrice, Comparator.nullsLast(Comparator.naturalOrder())).reversed());
            }
        }

        model.addAttribute("events", results);
        model.addAttribute("count", results.size());
        model.addAttribute("sort", sort);
        return "events/list";
    }
    @GetMapping("/events/{id}")
    public String showEventDetails(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Event event = repo.findById(id).orElse(null);
        if (event == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }
        model.addAttribute("event", event);
        return "events/details";
    }

    @ExceptionHandler(org.springframework.web.method.annotation.HandlerMethodValidationException.class)
    public String handleValidationException(Exception ex, Model model) {
        model.addAttribute("error", "Invalid search input. Please use only letters and up to 75 characters ");
        model.addAttribute("events", List.of());
        return "events/list";
    }


}