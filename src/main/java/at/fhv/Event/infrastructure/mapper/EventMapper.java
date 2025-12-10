package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final EquipmentMapper equipmentMapper;
    private final EquipmentJpaRepository equipmentJpa;

    public EventMapper(EquipmentMapper equipmentMapper,
                       EquipmentJpaRepository equipmentJpa) {
        this.equipmentMapper = equipmentMapper;
        this.equipmentJpa = equipmentJpa;
    }

    public Event toDomain(EventEntity e) {
        if (e == null) {
            return null;
        }

        var equipments = e.getEventEquipments().stream()
                .map(ee -> {
                    var newEq = equipmentJpa.findById(ee.getEquipment().getId()).orElseThrow();
                    return new EventEquipment(
                            equipmentMapper.toDomain(newEq),
                            ee.isRequired()
                    );
                })
                .collect(Collectors.toList());

        Event event = new Event(
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

        event.setCancelled(e.getCancelled() != null ? e.getCancelled() : false);
        return event;
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
        e.setCancelled(domain.getCancelled());
        e.setAudience(domain.getAudience());

        var eeEntities = domain.getEventEquipments().stream()
                .map(domEE -> {
                    var equipEntity = equipmentMapper.toEntity(domEE.getEquipment());
                    var ee = new EventEquipmentEntity();
                    ee.setEquipment(equipEntity);
                    ee.setRequired(domEE.isRequired());
                    ee.setEvent(e);
                    return ee;
                })
                .toList();

        e.getEventEquipments().clear();
        e.getEventEquipments().addAll(eeEntities);

        return e;
    }
}