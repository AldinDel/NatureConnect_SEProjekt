package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.rest.response.event.EventDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapperDTO {

    public EventDTO toDto(Event e) {
        if (e == null) return null;
        return new EventDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getLocation(),
                e.getPrice(),
                e.getCancelled(),
                mapEquipments(e.getEventEquipments())
        );
    }

    public EventDetailDTO toDetailDTO(Event e) {
        if (e == null) return null;

        List<Long> requiredIds = e.getEventEquipments().stream()
                .filter(EventEquipment::isRequired)
                .map(ee -> ee.getEquipment().getId())
                .collect(Collectors.toList());

        List<Long> optionalIds = e.getEventEquipments().stream()
                .filter(ee -> !ee.isRequired())
                .map(ee -> ee.getEquipment().getId())
                .collect(Collectors.toList());

        String organizerDisplay = getOrganizerDisplay(e.getOrganizer());

        return new EventDetailDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                organizerDisplay, // Angepasster Organisator-Name
                e.getCategory(),
                e.getDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getLocation(),
                e.getDifficulty(),
                e.getMinParticipants(),
                e.getMaxParticipants(),
                e.getPrice(),
                e.getImageUrl(),
                e.getCancelled(),
                mapEquipments(e.getEventEquipments()),
                requiredIds,
                optionalIds,
                e.getAudience() != null ? e.getAudience().toString() : null
        );
    }

    private List<EquipmentDTO> mapEquipments(List<EventEquipment> ees) {
        if (ees == null) return List.of();
        return ees.stream()
                .map(ee -> new EquipmentDTO(
                        ee.getEquipment().getId(),
                        ee.getEquipment().getName(),
                        ee.getEquipment().getUnitPrice(),
                        ee.getEquipment().isRentable(),
                        ee.isRequired(),
                        ee.getEquipment().getStock()
                ))
                .collect(Collectors.toList());
    }

    public EventOverviewDTO toOverview(Event e) {
        if (e == null) return null;

        String organizerDisplay = getOrganizerDisplay(e.getOrganizer());

        return new EventOverviewDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                organizerDisplay, // Angepasster Organisator-Name
                e.getCategory(),
                e.getDate(),
                e.getStartTime(),
                e.getEndTime(),
                e.getLocation(),
                e.getDifficulty() != null ? e.getDifficulty().toString() : null,
                e.getMinParticipants(),
                e.getMaxParticipants(),
                e.getPrice(),
                e.getImageUrl(),
                e.getAudience() != null ? e.getAudience().toString() : null,
                e.getCancelled()
        );
    }

    private String getOrganizerDisplay(String originalOrganizer) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth != null && auth.isAuthenticated()) {
            // Admin sieht immer den vollen Namen
            if (auth.getAuthorities().contains(new SimpleGrantedAuthority("ROLE_ADMIN"))) {
                return originalOrganizer;
            }
            // Der Organisator selbst sieht seinen Namen
            // (Hier vereinfacht über Namensvergleich, idealerweise über ID)
            // Wir nehmen an, dass der eingeloggte Benutzername (Email) nicht direkt mit dem Organisator-Namen übereinstimmt,
            // daher ist dieser Check hier schwierig ohne weitere DB-Abfragen.
            // Für dieses Szenario blenden wir es für alle anderen (inkl. Frontend und Customer) aus.
        }

        // Standard-Anzeige für alle anderen (Frontend, Customer, nicht eingeloggt)
        return "NatureConnect Team";
    }
}