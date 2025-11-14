package at.fhv.Event.rest.mapper;

import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.rest.response.equipment.EquipmentDTO;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventResponseMapper {

    private final EquipmentResponseMapper equipmentResponseMapper;

    public EventResponseMapper(EquipmentResponseMapper equipmentResponseMapper) {
        this.equipmentResponseMapper = equipmentResponseMapper;
    }

    public EventDetailDTO toDetailDTO(Event event) {

        List<EquipmentDTO> equipmentDTOs =
                event.getEventEquipments().stream()
                        .map(ee -> equipmentResponseMapper.toDTO(
                                ee.getEquipment(),
                                ee.isRequired()
                        ))
                        .collect(Collectors.toList());

        List<Long> requiredIds =
                event.getEventEquipments().stream()
                        .filter(ee -> ee.isRequired())
                        .map(ee -> ee.getEquipment().getId())
                        .toList();

        List<Long> optionalIds =
                event.getEventEquipments().stream()
                        .filter(ee -> !ee.isRequired())
                        .map(ee -> ee.getEquipment().getId())
                        .toList();

        return new EventDetailDTO(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getOrganizer(),
                event.getCategory(),
                event.getDate(),
                event.getStartTime(),
                event.getEndTime(),
                event.getLocation(),
                event.getDifficulty(),
                event.getMinParticipants(),
                event.getMaxParticipants(),
                event.getPrice(),
                event.getImageUrl(),
                event.getAudience(),
                event.getCancelled(),
                equipmentDTOs,
                requiredIds,
                optionalIds
        );
    }
}
