package at.fhv.Event.controller;

import at.fhv.Event.domain.model.equipment.Equipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.dto.EquipmentDTO;
import at.fhv.Event.dto.EventDTO;
import at.fhv.Event.persistence.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.math.BigDecimal;

import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.*;

@Controller
public class EventController {

    private final EventRepository repo;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private EventEquipmentRepository eventEquipmentRepository;

    @Autowired
    private EquipmentRepository equipmentRepo;

    public EventController(EventRepository repo) {
        this.repo = repo;
    }

    // Neues Event erstellen (Formular anzeigen)
    @GetMapping("/events/new")
    public String showCreateForm(Model model) {
        model.addAttribute("event", new Event());
        model.addAttribute("equipments", equipmentRepository.findAll());
        model.addAttribute("requiredIds", Collections.emptySet());
        model.addAttribute("isEdit", false);
        return "events/create_event";
    }

    // Event bearbeiten (Formular anzeigen)
    @GetMapping("/events/{id}/edit")
    public String showEditForm(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Event event = repo.findById(id).orElse(null);
        if (event == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        // Alle Equipments laden
        List<Equipment> allEquipments = equipmentRepository.findAll();

        // IDs von "required" Equipments für dieses Event
        event.getEventEquipments().size();
        List<Map<String, Object>> eventEquipments = event.getEventEquipments().stream()
                .map(eq -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", eq.getEquipment().getId());
                    map.put("name", eq.getEquipment().getName());
                    map.put("price", eq.getEquipment().getUnitPrice());
                    map.put("rentable", eq.getEquipment().isRentable());
                    map.put("required", eq.isRequired());
                    return map;
                })
                .toList();

// JSON-kompatible Liste an Thymeleaf übergeben
        model.addAttribute("equipmentJson", eventEquipments);
        model.addAttribute("event", event);
        model.addAttribute("isEdit", true);

        return "events/create_event";
    }

    // Neues Event speichern
    @PostMapping("/events")
    public String create(
            @ModelAttribute("event") Event event,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "equipmentNames", required = false) List<String> equipmentNames,
            @RequestParam(value = "equipmentPrices", required = false) List<BigDecimal> equipmentPrices,
            @RequestParam(value = "equipmentRentable", required = false) List<String> equipmentRentableFlags,
            @RequestParam(value = "requiredFlags", required = false) List<String> requiredFlags,
            RedirectAttributes ra) throws IOException {

        if (event.getEndTime() != null && event.getStartTime() != null
                && !event.getEndTime().isAfter(event.getStartTime())) {
            ra.addFlashAttribute("error", "End time must be after start time!");
            return "redirect:/events/new";
        }

        if (event.getMinParticipants() == null) event.setMinParticipants(1);
        if (event.getPrice() == null) event.setPrice(BigDecimal.ZERO);
        if (imageUrl != null && !imageUrl.isBlank()) event.setImageUrl(imageUrl.trim());

        repo.save(event);

        // Equipments speichern & verknüpfen
        if (equipmentNames != null && !equipmentNames.isEmpty()) {
            for (int i = 0; i < equipmentNames.size(); i++) {
                String name = equipmentNames.get(i);
                if (name == null || name.trim().isEmpty()) continue;

                BigDecimal price = (equipmentPrices != null && equipmentPrices.size() > i && equipmentPrices.get(i) != null)
                        ? equipmentPrices.get(i)
                        : BigDecimal.ZERO;

                boolean rentable = (equipmentRentableFlags != null && equipmentRentableFlags.size() > i);
                boolean required = (requiredFlags != null && requiredFlags.size() > i);

                Equipment eq = equipmentRepository.findByNameIgnoreCase(name.trim())
                        .orElseGet(() -> {
                            Equipment newEq = new Equipment();
                            newEq.setName(name.trim());
                            newEq.setUnitPrice(price);
                            newEq.setRentable(rentable);
                            return equipmentRepository.save(newEq);
                        });

                eventEquipmentRepository.save(new EventEquipment(event.getId(), eq.getId(), required));
            }
        }

        ra.addFlashAttribute("success", "Event created successfully!");
        return "redirect:/events";
    }

    // Bestehendes Event updaten
    @Transactional
    @PostMapping("/events/{id:[0-9]+}")
    public String updateEvent(@PathVariable("id") Long id,
                              @ModelAttribute("event") Event updatedEvent,
                              @RequestParam(value = "equipmentNames", required = false) List<String> equipmentNames,
                              @RequestParam(value = "equipmentPrices", required = false) List<BigDecimal> equipmentPrices,
                              @RequestParam(value = "equipmentRentable", required = false) List<String> equipmentRentableFlags,
                              @RequestParam(value = "requiredFlags", required = false) List<String> requiredFlags,
                              RedirectAttributes ra) {

        Event existingEvent = repo.findById(id).orElse(null);
        if (existingEvent == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }

        // Grunddaten aktualisieren
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
        existingEvent.setImageUrl(updatedEvent.getImageUrl());

        // Alte Verknüpfungen sicher entfernen
        List<EventEquipment> oldLinks = new ArrayList<>(existingEvent.getEventEquipments());
        existingEvent.getEventEquipments().clear(); // vom Event lösen
        eventEquipmentRepository.deleteAll(oldLinks); // dann DB löschen
        repo.flush(); // Persistence Context leeren, damit Hibernate nichts wieder anhängt

        // Neue Equipments hinzufügen
        if (equipmentNames != null && !equipmentNames.isEmpty()) {
            for (int i = 0; i < equipmentNames.size(); i++) {
                String name = equipmentNames.get(i);
                if (name == null || name.isBlank()) continue;

                BigDecimal price = (equipmentPrices != null && equipmentPrices.size() > i)
                        ? equipmentPrices.get(i)
                        : BigDecimal.ZERO;
                boolean rentable = (equipmentRentableFlags != null && equipmentRentableFlags.size() > i);
                boolean required = (requiredFlags != null && requiredFlags.size() > i);

                Equipment eq = equipmentRepository.findByNameIgnoreCase(name.trim())
                        .orElseGet(() -> {
                            Equipment newEq = new Equipment();
                            newEq.setName(name.trim());
                            newEq.setUnitPrice(price);
                            newEq.setRentable(rentable);
                            return equipmentRepository.save(newEq);
                        });

                EventEquipment ee = new EventEquipment(existingEvent.getId(), eq.getId(), required);
                ee.setEvent(existingEvent);
                ee.setEquipment(eq);
                existingEvent.getEventEquipments().add(ee);
            }
        }

        repo.saveAndFlush(existingEvent);
        ra.addFlashAttribute("success", "Event updated successfully!");
        return "redirect:/events";
    }


    @GetMapping("/api/events/{id}")
    @ResponseBody
    public ResponseEntity<?> getEventById(@PathVariable Long id) {
        Optional<Event> eventOpt = repo.findById(id);
        if (eventOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Event with ID " + id + " not found"));
        }

        Event event = eventOpt.get();

        // Equipments manuell initialisieren
        List<EquipmentDTO> equipmentList = event.getEventEquipments().stream()
                .map(eq -> new EquipmentDTO(
                        eq.getEquipment().getId(),
                        eq.getEquipment().getName(),
                        eq.getEquipment().getUnitPrice(),
                        eq.getEquipment().isRentable(),
                        eq.isRequired()
                ))
                .toList();

        EventDTO dto = new EventDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getLocation(),
                event.getPrice(),
                equipmentList
        );

        return ResponseEntity.ok(dto);
    }

    // Alle Events anzeigen
    @GetMapping("/events")
    public String showAllEvents(Model model) {
        model.addAttribute("events", repo.findAll());
        return "events/list";
    }

    // Such- und Filterfunktion
    @GetMapping("/events/search")
    public String searchEvents(
            @RequestParam(value = "q", required = false) String query,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam(value = "difficulty", required = false) String difficulty,
            @RequestParam(value = "minPrice", required = false) BigDecimal minPrice,
            @RequestParam(value = "maxPrice", required = false) BigDecimal maxPrice,
            @RequestParam(value = "startDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "source", required = false) String source, // von wo gesucht wurde (home oder list)
            Model model,
            RedirectAttributes redirectAttributes // für Popup-Nachrichten
    ) {

        // Datum prüfen — bevor irgendwas gesucht wird
        if ((startDate != null && startDate.isBefore(LocalDate.now())) ||
                (endDate != null && endDate.isBefore(LocalDate.now()))) {

            // Zeig Popup
            redirectAttributes.addFlashAttribute("error", "You cannot search for events in the past! ");

            // Bleib auf der richtigen Seite
            if ("home".equalsIgnoreCase(source)) {
                return "redirect:/"; // Homepage (nature_connect.html)
            } else {
                return "redirect:/events"; // Event-Liste
            }
        }

        // Wenn Datum ok → ganz normal weitermachen
        List<Event> events = repo.findAll();


        // Nach Suchbegriff filtern
        if (query != null && !query.isBlank()) {
            String qLower = query.toLowerCase();
            events = events.stream()
                    .filter(e ->
                            (e.getTitle() != null && e.getTitle().toLowerCase().contains(qLower)) ||
                                    (e.getDescription() != null && e.getDescription().toLowerCase().contains(qLower))
                    )
                    .toList();
        }

        // Nach Kategorie
        if (category != null && !category.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getCategory() != null && e.getCategory().equalsIgnoreCase(category))
                    .toList();
        }

        // Nach Location
        if (location != null && !location.isBlank()) {
            String locLower = location.toLowerCase();
            events = events.stream()
                    .filter(e -> e.getLocation() != null && e.getLocation().toLowerCase().contains(locLower))
                    .toList();
        }

        // Date Range (Datumsfilter werden hier eingefügt)
        if (startDate != null && endDate == null) {
            // nur genau dieses Datum
            events = events.stream()
                    .filter(e -> e.getDate() != null && e.getDate().equals(startDate))
                    .toList();
        } else {
            // Range-Filter: >= startDate, <= endDate (wenn gesetzt)
            if (startDate != null) {
                events = events.stream()
                        .filter(e -> e.getDate() != null && !e.getDate().isBefore(startDate))
                        .toList();
            }
            if (endDate != null) {
                events = events.stream()
                        .filter(e -> e.getDate() != null && !e.getDate().isAfter(endDate))
                        .toList();
            }
        }


        // Nach Difficulty
        if (difficulty != null && !difficulty.isBlank()) {
            events = events.stream()
                    .filter(e -> e.getDifficulty() != null &&
                            e.getDifficulty().name().equalsIgnoreCase(difficulty))
                    .toList();
        }

        // Nach Preis filtern
        if (minPrice != null) {
            events = events.stream()
                    .filter(e -> e.getPrice() != null && e.getPrice().compareTo(minPrice) >= 0)
                    .toList();
        }
        if (maxPrice != null) {
            events = events.stream()
                    .filter(e -> e.getPrice() != null && e.getPrice().compareTo(maxPrice) <= 0)
                    .toList();
        }


        // Sortieren
        if (sort != null) {
            switch (sort) {
                case "priceAsc" ->
                        events.sort(Comparator.comparing(Event::getPrice, Comparator.nullsLast(BigDecimal::compareTo)));
                case "priceDesc" ->
                        events.sort(Comparator.comparing(Event::getPrice, Comparator.nullsLast(BigDecimal::compareTo)).reversed());
                case "dateAsc" ->
                        events.sort(Comparator.comparing(Event::getDate, Comparator.nullsLast(LocalDate::compareTo)));
                case "dateDesc" ->
                        events.sort(Comparator.comparing(Event::getDate, Comparator.nullsLast(LocalDate::compareTo)).reversed());
            }
        }

        // Werte an View übergeben
        model.addAttribute("events", events);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("q", query);
        paramMap.put("category", category);
        paramMap.put("location", location);
        paramMap.put("difficulty", difficulty);
        paramMap.put("minPrice", minPrice);
        paramMap.put("maxPrice", maxPrice);
        model.addAttribute("param", paramMap);
        model.addAttribute("sort", sort);

        return "events/list";
    }


    // Einzelnes Event anzeigen
    @GetMapping("/events/{id:[0-9]+}")
    public String showEventDetails(@PathVariable("id") Long id, Model model, RedirectAttributes ra) {
        Event event = repo.findByIdWithEquipments(id)
                .orElseThrow(() -> new RuntimeException("Event not found"));
        if (event == null) {
            ra.addFlashAttribute("error", "Event not found!");
            return "redirect:/events";
        }
        model.addAttribute("event", event);
        return "events/create_event";
    }
}
