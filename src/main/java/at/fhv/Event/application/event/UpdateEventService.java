package at.fhv.Event.application.event;

import at.fhv.Event.application.request.event.UpdateEventRequest;
import at.fhv.Event.domain.model.event.EventAudience;
import at.fhv.Event.infrastructure.mapper.EventMapper;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentEntity;
import at.fhv.Event.infrastructure.persistence.equipment.EquipmentJpaRepository;
import at.fhv.Event.infrastructure.persistence.equipment.EventEquipmentEntity;
import at.fhv.Event.infrastructure.persistence.event.EventEntity;
import at.fhv.Event.infrastructure.persistence.event.EventJpaRepository;
import at.fhv.Event.rest.response.event.EventDetailDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateEventService {

    private final EventJpaRepository eventJpaRepository;
    private final EquipmentJpaRepository equipmentJpaRepository;
    private final EventMapper domainMapper;
    private final EventMapperDTO dtoMapper;

    public UpdateEventService(
            EventJpaRepository eventJpaRepository,
            EquipmentJpaRepository equipmentJpaRepository,
            EventMapper domainMapper,
            EventMapperDTO dtoMapper
    ) {
        this.eventJpaRepository = eventJpaRepository;
        this.equipmentJpaRepository = equipmentJpaRepository;
        this.domainMapper = domainMapper;
        this.dtoMapper = dtoMapper;
    }

    @Transactional
    public EventDetailDTO updateEvent(Long id, UpdateEventRequest req) {
        req.getEquipments().forEach(e -> System.out.println("Equipment: id=" + e.getId() + " name=" + e.getName() +
                " required=" + e.isRequired() + " rentable=" + e.isRentable()));

        EventEntity entity = eventJpaRepository.findByIdWithEquipments(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        entity.setTitle(req.getTitle());
        entity.setDescription(req.getDescription());
        entity.setOrganizer(req.getOrganizer());
        entity.setCategory(req.getCategory());
        entity.setDate(req.getDate());
        entity.setStartTime(req.getStartTime());
        entity.setEndTime(req.getEndTime());
        entity.setLocation(req.getLocation());
        entity.setDifficulty(req.getDifficulty());
        entity.setMinParticipants(req.getMinParticipants());
        entity.setMaxParticipants(req.getMaxParticipants());
        entity.setPrice(req.getPrice());
        entity.setImageUrl(req.getImageUrl());

        if (req.getAudience() != null && !req.getAudience().isBlank()) {
            entity.setAudience(EventAudience.valueOf(req.getAudience()));
        } else {
            entity.setAudience(null);
        }

        if (req.getEquipments() != null) {
            req.getEquipments().forEach(eq -> {
                if (eq.isRentable()) {
                    if (eq.getUnitPrice() == null) {
                        throw new IllegalArgumentException(
                                "Unit price is required when equipment is rentable: " + eq.getName()
                        );
                    }
                    if (eq.getStock() == null) {
                        throw new IllegalArgumentException(
                                "Stock is required when equipment is rentable: " + eq.getName()
                        );
                    }
                }
            });
        }

        var oldLinks = new java.util.ArrayList<>(entity.getEventEquipments());
        for (var link : oldLinks) {
            entity.removeEquipment(link);
        }
        eventJpaRepository.flush();
        if (req.getEquipments() != null && !req.getEquipments().isEmpty()) {

            for (var eqReq : req.getEquipments()) {
                EquipmentEntity equipmentEntity;

                if (eqReq.getId() == null) {
                    System.out.println("→ Creating NEW equipment (no id): " + eqReq.getName());

                    equipmentEntity = new EquipmentEntity();
                    equipmentEntity.setName(eqReq.getName());
                    equipmentEntity.setRentable(eqReq.isRentable());

                    if (eqReq.isRentable()) {
                        equipmentEntity.setUnitPrice(eqReq.getUnitPrice());
                        equipmentEntity.setStock(eqReq.getStock());
                    } else {
                        equipmentEntity.setUnitPrice(null);
                        equipmentEntity.setStock(null);
                    }

                    equipmentJpaRepository.save(equipmentEntity);
                    System.out.println("Created new equipment with id: " + equipmentEntity.getId());
                }

                else {
                    System.out.println("Updating existing equipment id=" + eqReq.getId());

                    equipmentEntity = equipmentJpaRepository.findById(eqReq.getId())
                            .orElseThrow(() -> new RuntimeException("Equipment not found: " + eqReq.getId()));


                    equipmentEntity.setRentable(eqReq.isRentable());

                    if (eqReq.isRentable()) {
                        equipmentEntity.setUnitPrice(eqReq.getUnitPrice());
                        equipmentEntity.setStock(eqReq.getStock());
                    } else {
                        equipmentEntity.setUnitPrice(null);
                        equipmentEntity.setStock(null);
                    }

                    System.out.println("  ✓ Updated: rentable=" + equipmentEntity.isRentable() +
                            " price=" + equipmentEntity.getUnitPrice() +
                            " stock=" + equipmentEntity.getStock());
                }

                var link = new EventEquipmentEntity(entity, equipmentEntity, eqReq.isRequired());
                entity.addEquipment(link);

                System.out.println("  ✓ Linked to event with required=" + eqReq.isRequired());
            }
        }

        EventEntity saved = eventJpaRepository.save(entity);
        return dtoMapper.toDetailDTO(domainMapper.toDomain(saved));
    }
}