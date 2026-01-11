package at.fhv.Event.infrastructure.mapper;

import at.fhv.Event.domain.model.equipment.EventEquipment;
import at.fhv.Event.domain.model.event.Event;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        if (e == null) return null;

        var equipments = e.getEventEquipments().stream()
                .map(ee -> new EventEquipment(
                        equipmentMapper.toDomain(ee.getEquipment()),
                        ee.isRequired()
                ))
                .collect(Collectors.toSet());

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
                equipments,
                new ArrayList<>(e.getHikeRouteKeys())
        );

        event.setCancelled(e.getCancelled());

        event.setRecurring(e.isRecurring());
        event.setRecurrenceStart(e.getRecurrenceStart());
        event.setRecurrenceEnd(e.getRecurrenceEnd());
        event.setRecurrenceDays(e.getRecurrenceDays());

        return event;
    }

    public List<Event> toDomainList(List<EventEntity> entities) {
        if (entities == null) {
            return Collections.emptyList();
        }

        return entities.stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
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

        e.setRecurring(domain.isRecurring());
        e.setRecurrenceStart(domain.getRecurrenceStart());
        e.setRecurrenceEnd(domain.getRecurrenceEnd());
        e.setRecurrenceDays(domain.getRecurrenceDays());

        if (domain.getHikeRouteKeys() != null) {
            e.getHikeRouteKeys().clear();
            e.getHikeRouteKeys().addAll(domain.getHikeRouteKeys());
        }

        var eeEntities = domain.getEventEquipments().stream()
                .map(domEE -> {
                    var equipEntity = equipmentJpa
                            .findById(domEE.getEquipment().getId())
                            .orElseThrow();

                    var ee = new EventEquipmentEntity();
                    ee.setEquipment(equipEntity);
                    ee.setRequired(domEE.isRequired());
                    ee.setEvent(e);
                    return ee;
                })
                .collect(Collectors.toCollection(ArrayList::new));

        e.getEventEquipments().clear();
        e.getEventEquipments().addAll(eeEntities);

        return e;
    }

    public void applyToExistingEntity(Event domain, EventEntity e) {

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

        e.setRecurring(domain.isRecurring());
        e.setRecurrenceStart(domain.getRecurrenceStart());
        e.setRecurrenceEnd(domain.getRecurrenceEnd());

        e.getRecurrenceDays().clear();
        if (domain.getRecurrenceDays() != null) {
            e.getRecurrenceDays().addAll(domain.getRecurrenceDays());
        }

        if (domain.getHikeRouteKeys() != null) {
            e.getHikeRouteKeys().clear();
            e.getHikeRouteKeys().addAll(domain.getHikeRouteKeys());
        }

        e.getEventEquipments().clear();
        if (domain.getEventEquipments() != null) {
            for (var domEE : domain.getEventEquipments()) {
                var equipEntity = equipmentJpa
                        .findById(domEE.getEquipment().getId())
                        .orElseThrow();

                var ee = new EventEquipmentEntity();
                ee.setEquipment(equipEntity);
                ee.setRequired(domEE.isRequired());
                ee.setEvent(e);

                e.getEventEquipments().add(ee);
            }
        }
    }
}
