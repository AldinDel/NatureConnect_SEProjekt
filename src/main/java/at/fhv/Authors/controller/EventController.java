package at.fhv.Authors.controller;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;

@Controller
public class EventController {

    @GetMapping("/events/search")
    public String search(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String location,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) String activity,
            Model model) {

        // Temporäre Ausgabe in der Konsole zur Kontrolle
        System.out.println("Suchparameter: " + q + ", " + location + ", " + date + ", " + activity);

        // Füge Dummy-Daten hinzu (nur zu Testzwecken)
        model.addAttribute("events", java.util.Collections.emptyList());

        // Diese Seite wird nach der Suche geladen (später anlegen)
        return "events/list";
    }

    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        // Optional: model.addAttribute("event", new EventDto());
        return "events/create_event"; // lädt templates/events/create_event.html
    }


}

