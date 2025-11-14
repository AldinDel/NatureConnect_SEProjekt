package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final EquipmentMapper equipmentMapper;

    public EventMapper(EquipmentMapper equipmentMapper) {
        this.equipmentMapper = equipmentMapper;
    }

    public Event toDomain(EventEntity e) {
        if (e == null) return null;
        var equipments = e.getEventEquipments().stream()
                .map(ee -> new EventEquipment(
                        equipmentMapper.toDomain(ee.getEquipment()),
                        ee.isRequired()
                ))
                .collect(Collectors.toList());

        return new Event(
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
                e.getAudience(),
                equipments
        );
    }

    public EventEntity toEntity(Event domain) {
        if (domain == null) return null;
        EventEntity e = new EventEntity();
        e.setId(domain.getId());
        e.setTitle(domain.getTitle());
        e.setDescription(domain.getDescription());
        e.setOrganizer(domain.getOrganizer());
        e.setCategory(domain.getCategory());
        e.setDate(domain.getDate());
        e.setStartTime(domain.getStartTime());
        e.setEndTime(domain.getEndTime());
        e.setLocation(domain.getLocation());
        e.setDifficulty(domain.getDifficulty());
        e.setMinParticipants(domain.getMinParticipants());
        e.setMaxParticipants(domain.getMaxParticipants());
        e.setPrice(domain.getPrice());
        e.setImageUrl(domain.getImageUrl());
        e.setAudience(domain.getAudience());
        e.setCancelled(domain.getCancelled());


        // build EventEquipmentEntity list; DO NOT set composite id with nulls; JPA will handle MapsId
        var eeEntities = domain.getEventEquipments().stream()
                .map(domEE -> {
                    var equipEntity = equipmentMapper.toEntity(domEE.getEquipment());
                    var ee = new EventEquipmentEntity();
                    ee.setEquipment(equipEntity);
                    ee.setRequired(domEE.isRequired());
                    ee.setEvent(e); // associate parent -> necessary for MapsId to work later
                    return ee;
                })
                .collect(Collectors.toList());
        // clear and add (or set)
        e.getEventEquipments().clear();
        e.getEventEquipments().addAll(eeEntities);

        return e;
    }
}
