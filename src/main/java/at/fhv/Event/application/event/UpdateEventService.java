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
        System.out.println("=== UPDATE EVENT SERVICE ===");
        req.getEquipments().forEach(e -> {
            System.out.println("Equipment: id=" + e.getId() + " name=" + e.getName() +
                    " required=" + e.isRequired() + " rentable=" + e.isRentable());
        });
        System.out.println("===========================");

        EventEntity entity = eventJpaRepository.findByIdWithEquipments(id)
                .orElseThrow(() -> new RuntimeException("Event not found: " + id));

        // BASIC FIELDS
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

        // REMOVE ALL OLD LINKS
        var oldLinks = new java.util.ArrayList<>(entity.getEventEquipments());
        for (var link : oldLinks) {
            entity.removeEquipment(link);
        }
        eventJpaRepository.flush();

        // ADD NEW/UPDATED EQUIPMENTS
        if (req.getEquipments() != null && !req.getEquipments().isEmpty()) {

            for (var eqReq : req.getEquipments()) {
                EquipmentEntity equipmentEntity;

                // CASE 1: NEW EQUIPMENT (id is null)
                if (eqReq.getId() == null) {
                    System.out.println("→ Creating NEW equipment: " + eqReq.getName());

                    // Check if equipment with this name already exists
                    var existingEquipment = equipmentJpaRepository.findByNameIgnoreCase(eqReq.getName());

                    if (existingEquipment.isPresent()) {
                        // Equipment already exists, just use it
                        System.out.println("  ✓ Equipment '" + eqReq.getName() + "' already exists, reusing it");
                        equipmentEntity = existingEquipment.get();

                        // Update rentable flag if needed
                        equipmentEntity.setRentable(eqReq.isRentable());
                        equipmentEntity.setUnitPrice(eqReq.getUnitPrice());
                    } else {
                        // Create new equipment
                        equipmentEntity = new EquipmentEntity();
                        equipmentEntity.setName(eqReq.getName());
                        equipmentEntity.setUnitPrice(eqReq.getUnitPrice());
                        equipmentEntity.setRentable(eqReq.isRentable());
                        equipmentJpaRepository.save(equipmentEntity);
                        System.out.println("  ✓ Created new equipment with id: " + equipmentEntity.getId());
                    }
                }
                // CASE 2: EXISTING EQUIPMENT (id is provided)
                else {
                    System.out.println("→ Updating existing equipment id=" + eqReq.getId());

                    equipmentEntity = equipmentJpaRepository.findById(eqReq.getId())
                            .orElseThrow(() -> new RuntimeException("Equipment not found: " + eqReq.getId()));

                    // Update editable fields (NOT the name, as it's unique)
                    equipmentEntity.setUnitPrice(eqReq.getUnitPrice());
                    equipmentEntity.setRentable(eqReq.isRentable());

                    System.out.println("  ✓ Updated: rentable=" + equipmentEntity.isRentable() +
                            " price=" + equipmentEntity.getUnitPrice());
                }

                // LINK EVENT <-> EQUIPMENT with the 'required' flag
                var link = new EventEquipmentEntity(entity, equipmentEntity, eqReq.isRequired());
                entity.addEquipment(link);

                System.out.println("  ✓ Linked to event with required=" + eqReq.isRequired());
            }
        }

        EventEntity saved = eventJpaRepository.save(entity);
        System.out.println("=== EVENT SAVED ===");
        return dtoMapper.toDetailDTO(domainMapper.toDomain(saved));
    }
}