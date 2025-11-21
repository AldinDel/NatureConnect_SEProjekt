package at.fhv.Event.application.event;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.rest.response.event.EventDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import at.fhv.Event.rest.response.event.EventOverviewDTO;
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

        return new EventDetailDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
                e.getOrganizer(),
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

        return new EventOverviewDTO(
                e.getId(),
                e.getTitle(),
                e.getDescription(),
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
}

