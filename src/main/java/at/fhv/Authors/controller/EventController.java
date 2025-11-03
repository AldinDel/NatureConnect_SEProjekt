package at.fhv.Authors.controller;

import at.fhv.Authors.domain.model.Event;
import at.fhv.Authors.persistence.EventRepository;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;

@Controller
public class EventController {

    private final EventRepository repo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

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

    // formular anzeigen
    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event()); // wichtig für th:object
        return "events/create_event"; // lädt templates/events/create_event.html
    }

    // event liste
    @GetMapping("/events")
    public String list(Model model) {
        model.addAttribute("events", repo.findAll());
        return "events/list"; // templates/events/list.html
    }

    // speichern von formular
    @PostMapping("/events")
    public String create(@ModelAttribute("event") at.fhv.Authors.domain.model.Event event,
                         @RequestParam(value = "photo", required = false)
                         org.springframework.web.multipart.MultipartFile photo,
                         org.springframework.web.servlet.mvc.support.RedirectAttributes ra) throws java.io.IOException {

        // bild speichern (optional)
        if (photo != null && !photo.isEmpty()) {
            java.nio.file.Path uploadDir = java.nio.file.Paths.get("uploads");
            java.nio.file.Files.createDirectories(uploadDir);

            String ext = org.springframework.util.StringUtils.getFilenameExtension(photo.getOriginalFilename());
            String filename = java.util.UUID.randomUUID().toString() + (ext != null ? "." + ext : "");
            java.nio.file.Path target = uploadDir.resolve(filename);

            try (var in = photo.getInputStream()) {
                java.nio.file.Files.copy(in, target, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }

            // Pfad für das Frontend setzen
            event.setImageUrl("/uploads/" + filename);
        }

        repo.save(event);
        ra.addFlashAttribute("success", "Event wurde angelegt.");
        return "redirect:/events";
    }

    @GetMapping("/events/overview")
    public String overviewAlias() {
        return "redirect:/events";
    }


}
